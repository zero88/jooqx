package io.zero88.jooqx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.jooq.DDLQuery;
import org.jooq.DSLContext;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.Transaction;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.ArrayTuple;
import io.zero88.jooqx.MiscImpl.BatchResultImpl;
import io.zero88.jooqx.SQLImpl.SQLEI;
import io.zero88.jooqx.SQLImpl.SQLExecutorBuilderImpl;
import io.zero88.jooqx.SQLImpl.SQLPQ;
import io.zero88.jooqx.adapter.RowConverterStrategy;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.adapter.SelectStrategy;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

final class JooqxSQLImpl {

    static final class ReactiveSQLPQ extends SQLPQ<Tuple> implements JooqxPreparedQuery {

        protected ArrayTuple doConvert(Map<String, Param<?>> params, DataTypeMapperRegistry registry,
                                       BiFunction<String, Param<?>, ?> queryValue) {
            final ArrayTuple bindValues = new ArrayTuple(params.size());
            params.entrySet()
                  .stream()
                  .filter(entry -> !entry.getValue().isInline())
                  .forEachOrdered(
                      etr -> bindValues.addValue(registry.toDatabaseType(etr.getKey(), etr.getValue(), queryValue)));
            return bindValues;
        }

    }


    static class ReactiveSQLRC implements JooqxResultCollector {

        @Override
        public @NotNull <T, R> List<R> collect(@NotNull RowSet<Row> resultSet,
                                               @NotNull RowConverterStrategy<T, R> strategy) {
            return doCollect(resultSet, strategy, strategy.strategy());
        }

        @NotNull
        protected <T, R> List<R> doCollect(RowSet<Row> resultSet, RowConverterStrategy<T, R> strategy,
                                           SelectStrategy selectStrategy) {
            final List<R> records = new ArrayList<>();
            final RowIterator<Row> iterator = resultSet.iterator();
            if (selectStrategy == SelectStrategy.MANY) {
                iterator.forEachRemaining(row -> records.add(toRecord(strategy, row)));
            } else if (iterator.hasNext()) {
                records.add(toRecord(strategy, iterator.next()));
                warnManyResult(iterator.hasNext(), strategy.strategy());
            }
            return records;
        }

        private <T, R> R toRecord(@NotNull RowConverterStrategy<T, R> strategy, @NotNull Row row) {
            return IntStream.range(0, row.size())
                            .mapToObj(row::getColumnName)
                            .map(strategy::lookupField)
                            .filter(Objects::nonNull)
                            .collect(strategy.createCollector(f -> row.getValue(f.getName())));
        }

    }


    static final class ReactiveSQLBC extends ReactiveSQLRC implements JooqxBatchCollector {

        @Override
        public @NotNull <T, R> List<R> collect(@NotNull RowSet<Row> resultSet,
                                               @NotNull RowConverterStrategy<T, R> strategy) {
            final List<R> records = new ArrayList<>();
            while (resultSet != null) {
                final List<R> rows = doCollect(resultSet, strategy, SelectStrategy.FIRST_ONE);
                if (!rows.isEmpty()) {
                    records.add(rows.get(0));
                }
                resultSet = resultSet.next();
            }
            return records;
        }

        @Override
        public int batchResultSize(@NotNull RowSet<Row> batchResult) {
            final int[] count = new int[] { 0 };
            while (batchResult != null) {
                count[0]++;
                batchResult = batchResult.next();
            }
            return count[0];
        }

    }


    static class JooqxConnImpl extends JooqxImpl<SqlConnection> implements JooqxTx {

        private final Jooqx delegate;

        JooqxConnImpl(Vertx vertx, DSLContext dsl, SqlConnection sqlClient, JooqxPreparedQuery preparedQuery,
                      JooqxResultCollector resultCollector, SQLErrorConverter errorConverter,
                      DataTypeMapperRegistry typeMapperRegistry) {
            super(vertx, dsl, sqlClient, preparedQuery, resultCollector, errorConverter, typeMapperRegistry);
            this.delegate = null;
        }

        JooqxConnImpl(@NotNull Jooqx delegate) {
            super(delegate.vertx(), delegate.dsl(), null, delegate.preparedQuery(), delegate.resultCollector(),
                  delegate.errorConverter(), delegate.typeMapperRegistry());
            this.delegate = delegate;
        }

        @Override
        @SuppressWarnings("unchecked")
        public @NotNull JooqxTx transaction() {
            return delegate().transaction();
        }

        @Override
        public <X> Future<@Nullable X> run(@NotNull Function<JooqxTx, Future<X>> function) {
            return delegate().sqlClient()
                             .getConnection()
                             .compose(conn -> beginTx(conn, function), t -> Future.failedFuture(
                                 transientConnFailed("Unable to open SQL connection", t)));
        }

        @Override
        protected JooqxTx withSqlClient(SqlConnection sqlClient) {
            return new JooqxConnImpl(vertx(), dsl(), sqlClient, preparedQuery(), resultCollector(), errorConverter(),
                                     typeMapperRegistry());
        }

        private <X> Future<X> beginTx(@NotNull SqlConnection conn, @NotNull Function<JooqxTx, Future<X>> transaction) {
            return conn.begin()
                       .flatMap(tx -> transaction.apply(withSqlClient(conn))
                                                 .compose(res -> tx.commit().flatMap(v -> Future.succeededFuture(res)),
                                                          err -> rollbackHandler(tx, errorConverter().handle(err))))
                       .onComplete(ar -> conn.close());
        }

        private <X> Future<X> rollbackHandler(@NotNull Transaction tx, @NotNull RuntimeException t) {
            return tx.rollback().compose(v -> Future.failedFuture(t), failure -> Future.failedFuture(t));
        }

        private Jooqx delegate() {
            if (Objects.isNull(delegate)) {
                throw new UnsupportedOperationException(
                    "Unsupported using connection on SQL connection: [" + sqlClient().getClass() +
                    "]. Switch using SQL pool");
            }
            return delegate;
        }

    }


    static class JooqxPoolImpl extends JooqxImpl<Pool> implements Jooqx {

        JooqxPoolImpl(Vertx vertx, DSLContext dsl, Pool sqlClient, JooqxPreparedQuery preparedQuery,
                      JooqxResultCollector resultCollector, SQLErrorConverter errorConverter,
                      DataTypeMapperRegistry typeMapperRegistry) {
            super(vertx, dsl, sqlClient, preparedQuery, resultCollector, errorConverter, typeMapperRegistry);
        }

        @Override
        @NotNull
        @SuppressWarnings("unchecked")
        public JooqxTx transaction() {
            return new JooqxConnImpl(this);
        }

        @Override
        protected Jooqx withSqlClient(Pool sqlClient) {
            return new JooqxPoolImpl(vertx(), dsl(), sqlClient, preparedQuery(), resultCollector(), errorConverter(),
                                     typeMapperRegistry());
        }

    }


    abstract static class JooqxImpl<S extends SqlClient>
        extends SQLEI<S, Tuple, JooqxPreparedQuery, RowSet<Row>, JooqxResultCollector> implements JooqxBase<S> {

        JooqxImpl(Vertx vertx, DSLContext dsl, S sqlClient, JooqxPreparedQuery preparedQuery,
                  JooqxResultCollector resultCollector, SQLErrorConverter errorConverter,
                  DataTypeMapperRegistry typeMapperRegistry) {
            super(vertx, dsl, sqlClient, preparedQuery, resultCollector, errorConverter, typeMapperRegistry);
            tweakDSLSetting();
        }

        private void tweakDSLSetting() {
            final Settings settings = dsl().configuration().settings();
            try {
                final Class<?> pgConn = JooqxImpl.class.getClassLoader().loadClass("io.vertx.pgclient.PgConnection");
                final Class<?> pgPool = JooqxImpl.class.getClassLoader().loadClass("io.vertx.pgclient.PgPool");
                if (pgConn.isInstance(sqlClient()) || pgPool.isInstance(sqlClient())) {
                    if (settings.getParamType() != ParamType.INDEXED) {
                        return;
                    }
                    settings.setParamType(ParamType.NAMED);
                }
            } catch (ClassNotFoundException ex) {
                //nothing happens with another SQL client
            }
        }

        @Override
        public <T, R> Future<R> execute(@NotNull Query query, @NotNull SQLResultAdapter<T, R> adapter) {
            return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                              .execute(preparedQuery().bindValues(query, typeMapperRegistry()))
                              .map(rs -> adapter.collect(rs, resultCollector(), dsl(), typeMapperRegistry()))
                              .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public Future<BatchResult> batch(@NotNull Query query, @NotNull BindBatchValues bindBatchValues) {
            return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                              .executeBatch(preparedQuery().bindValues(query, bindBatchValues, typeMapperRegistry()))
                              .map(r -> new ReactiveSQLBC().batchResultSize(r))
                              .map(s -> BatchResultImpl.create(bindBatchValues.size(), s))
                              .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public <T, R> Future<BatchReturningResult<R>> batch(@NotNull Query query,
                                                            @NotNull BindBatchValues bindBatchValues,
                                                            @NotNull SQLResultAdapter.SQLResultListAdapter<T, R> adapter) {
            return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                              .executeBatch(preparedQuery().bindValues(query, bindBatchValues, typeMapperRegistry()))
                              .map(rs -> adapter.collect(rs, new ReactiveSQLBC(), dsl(), typeMapperRegistry()))
                              .map(rs -> BatchResultImpl.create(bindBatchValues.size(), rs))
                              .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public Future<Integer> ddl(@NotNull DDLQuery query) {
            return sqlClient().query(preparedQuery().sql(dsl().configuration(), query))
                              .execute()
                              .map(SqlResult::size)
                              .otherwise(errorConverter()::reThrowError);
        }

        @Override
        protected @NotNull JooqxPreparedQuery defPrepareQuery() { return JooqxPreparedQuery.create(); }

        @Override
        protected @NotNull JooqxResultCollector defResultCollector() { return JooqxResultCollector.create(); }

    }


    static class JooqxBuilderImpl
        extends SQLExecutorBuilderImpl<Pool, Tuple, JooqxPreparedQuery, RowSet<Row>, JooqxResultCollector, JooqxBuilder>
        implements JooqxBuilder {

        @Override
        public @NotNull Jooqx build() {
            return new JooqxPoolImpl(vertx(), dsl(), sqlClient(), preparedQuery(), resultCollector(), errorConverter(),
                                     typeMapperRegistry());
        }

    }


    static class JooqxConnBuilderImpl extends
                                      SQLExecutorBuilderImpl<SqlConnection, Tuple, JooqxPreparedQuery, RowSet<Row>,
                                                                JooqxResultCollector, JooqxConnBuilder>
        implements JooqxConnBuilder {

        @Override
        public @NotNull JooqxConn build() {
            return new JooqxConnImpl(vertx(), dsl(), sqlClient(), preparedQuery(), resultCollector(), errorConverter(),
                                     typeMapperRegistry());
        }

    }

}