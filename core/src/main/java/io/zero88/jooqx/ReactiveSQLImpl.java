package io.zero88.jooqx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
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
import io.vertx.sqlclient.Transaction;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.ArrayTuple;
import io.zero88.jooqx.MiscImpl.BatchResultImpl;
import io.zero88.jooqx.SQLImpl.SQLEI;
import io.zero88.jooqx.SQLImpl.SQLPQ;
import io.zero88.jooqx.adapter.RowConverterStrategy;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.adapter.SelectStrategy;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

final class ReactiveSQLImpl {

    static final class ReactiveSQLPQ extends SQLPQ<Tuple> implements ReactiveSQLPreparedQuery {

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


    static class ReactiveSQLRC implements ReactiveSQLResultCollector {

        @Override
        public @NonNull <T, R> List<R> collect(@NonNull RowSet<Row> resultSet,
                                               @NonNull RowConverterStrategy<T, R> strategy) {
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

        private <T, R> R toRecord(@NonNull RowConverterStrategy<T, R> strategy, @NonNull Row row) {
            return IntStream.range(0, row.size())
                            .mapToObj(row::getColumnName)
                            .map(strategy::lookupField)
                            .filter(Objects::nonNull)
                            .collect(strategy.createCollector(f -> row.getValue(f.getName())));
        }

    }


    static final class ReactiveSQLBC extends ReactiveSQLRC implements ReactiveSQLBatchCollector {

        @Override
        public @NonNull <T, R> List<R> collect(@NonNull RowSet<Row> resultSet,
                                               @NonNull RowConverterStrategy<T, R> strategy) {
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
        public int batchResultSize(@NonNull RowSet<Row> batchResult) {
            final int[] count = new int[] {0};
            while (batchResult != null) {
                count[0]++;
                batchResult = batchResult.next();
            }
            return count[0];
        }

    }


    static class ConnectionJooqxImpl extends ReactiveJooqxImpl<SqlConnection> implements ReactiveJooqxTx {

        private final ReactivePoolJooqx delegate;

        ConnectionJooqxImpl(Vertx vertx, DSLContext dsl, SqlConnection sqlClient,
                            ReactiveSQLPreparedQuery preparedQuery, ReactiveSQLResultCollector resultCollector,
                            SQLErrorConverter errorConverter, DataTypeMapperRegistry typeMapperRegistry) {
            super(vertx, dsl, sqlClient, preparedQuery, resultCollector, errorConverter, typeMapperRegistry);
            this.delegate = null;
        }

        public ConnectionJooqxImpl(@NonNull ReactivePoolJooqx delegate) {
            super(delegate.vertx(), delegate.dsl(), null, delegate.preparedQuery(), delegate.resultCollector(),
                  delegate.errorConverter(), delegate.typeMapperRegistry());
            this.delegate = delegate;
        }

        @Override
        @SuppressWarnings("unchecked")
        public @NonNull ReactiveJooqxTx transaction() {
            return delegate().transaction();
        }

        @Override
        public <X> Future<@Nullable X> run(@NonNull Function<ReactiveJooqxTx, Future<X>> function) {
            return delegate().sqlClient()
                             .getConnection()
                             .compose(conn -> beginTx(conn, function), t -> Future.failedFuture(
                                 transientConnFailed("Unable to open SQL connection", t)));
        }

        private <X> Future<X> beginTx(@NonNull SqlConnection conn,
                                      @NonNull Function<ReactiveJooqxTx, Future<X>> transaction) {
            return conn.begin()
                       .flatMap(tx -> transaction.apply((ReactiveJooqxTx) withSqlClient(conn))
                                                 .compose(res -> tx.commit().flatMap(v -> Future.succeededFuture(res)),
                                                          err -> rollbackHandler(tx, errorConverter().handle(err))))
                       .onComplete(ar -> conn.close());
        }

        private <X> Future<X> rollbackHandler(@NonNull Transaction tx, @NonNull RuntimeException t) {
            return tx.rollback().compose(v -> Future.failedFuture(t), failure -> Future.failedFuture(t));
        }

        private ReactivePoolJooqx delegate() {
            if (Objects.isNull(delegate)) {
                throw new UnsupportedOperationException(
                    "Unsupported using connection on SQL connection: [" + sqlClient().getClass() +
                    "]. Switch using SQL pool");
            }
            return delegate;
        }

    }


    static class PoolJooqxImpl extends ReactiveJooqxImpl<Pool> implements ReactivePoolJooqx {

        PoolJooqxImpl(Vertx vertx, DSLContext dsl, Pool sqlClient, ReactiveSQLPreparedQuery preparedQuery,
                      ReactiveSQLResultCollector resultCollector, SQLErrorConverter errorConverter,
                      DataTypeMapperRegistry typeMapperRegistry) {
            super(vertx, dsl, sqlClient, preparedQuery, resultCollector, errorConverter, typeMapperRegistry);
        }

        @Override
        @NonNull
        @SuppressWarnings("unchecked")
        public ReactiveJooqxTx transaction() {
            return new ConnectionJooqxImpl(this);
        }

    }


    @Getter
    @Accessors(fluent = true)
    abstract static class ReactiveJooqxImpl<S extends SqlClient>
        extends SQLEI<S, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>, ReactiveSQLResultCollector>
        implements ReactiveJooqx<S> {

        ReactiveJooqxImpl(Vertx vertx, DSLContext dsl, S sqlClient, ReactiveSQLPreparedQuery preparedQuery,
                          ReactiveSQLResultCollector resultCollector, SQLErrorConverter errorConverter,
                          DataTypeMapperRegistry typeMapperRegistry) {
            super(vertx, dsl, sqlClient, preparedQuery, resultCollector, errorConverter, typeMapperRegistry);
            tweakDSLSetting();
        }

        private void tweakDSLSetting() {
            final Settings settings = dsl().configuration().settings();
            try {
                final Class<?> pgConn = ReactiveJooqxImpl.class.getClassLoader()
                                                               .loadClass("io.vertx.pgclient.PgConnection");
                final Class<?> pgPool = ReactiveJooqxImpl.class.getClassLoader().loadClass("io.vertx.pgclient.PgPool");
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
        public <T, R> Future<R> execute(@NonNull Query query, @NonNull SQLResultAdapter<T, R> adapter) {
            return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                              .execute(preparedQuery().bindValues(query, typeMapperRegistry()))
                              .map(rs -> adapter.collect(rs, resultCollector(), dsl(), typeMapperRegistry()))
                              .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public Future<BatchResult> batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues) {
            return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                              .executeBatch(preparedQuery().bindValues(query, bindBatchValues, typeMapperRegistry()))
                              .map(r -> new ReactiveSQLBC().batchResultSize(r))
                              .map(s -> BatchResultImpl.create(bindBatchValues.size(), s))
                              .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public <T, R> Future<BatchReturningResult<R>> batch(@NonNull Query query,
                                                            @NonNull BindBatchValues bindBatchValues,
                                                            @NonNull SQLResultAdapter.SQLResultListAdapter<T, R> adapter) {
            return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                              .executeBatch(preparedQuery().bindValues(query, bindBatchValues, typeMapperRegistry()))
                              .map(rs -> adapter.collect(rs, new ReactiveSQLBC(), dsl(), typeMapperRegistry()))
                              .map(rs -> BatchResultImpl.create(bindBatchValues.size(), rs))
                              .otherwise(errorConverter()::reThrowError);
        }

        @Override
        protected ReactiveJooqxImpl<S> withSqlClient(@NonNull S sqlClient) {
            return (ReactiveJooqxImpl<S>) ReactiveJooqx.<S>builder().vertx(vertx())
                                                                    .sqlClient(sqlClient)
                                                                    .dsl(dsl())
                                                                    .preparedQuery(preparedQuery())
                                                                    .resultCollector(resultCollector())
                                                                    .errorConverter(errorConverter())
                                                                    .typeMapperRegistry(typeMapperRegistry())
                                                                    .build();
        }

        @Override
        protected ReactiveSQLPreparedQuery defPrepareQuery() { return new ReactiveSQLPQ(); }

        @Override
        protected ReactiveSQLResultCollector defResultCollector() { return new ReactiveSQLRC(); }

    }

}
