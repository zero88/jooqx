package io.github.zero88.jooqx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.jooq.DDLQuery;
import org.jooq.DSLContext;
import org.jooq.Param;
import org.jooq.Parameter;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Routine;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;

import io.github.zero88.jooqx.SQLImpl.SQLEI;
import io.github.zero88.jooqx.SQLImpl.SQLExecutorBuilderImpl;
import io.github.zero88.jooqx.SQLImpl.SQLPQ;
import io.github.zero88.jooqx.adapter.FieldWrapper;
import io.github.zero88.jooqx.adapter.RecordFactory;
import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.github.zero88.jooqx.adapter.SQLResultListAdapter;
import io.github.zero88.jooqx.adapter.SelectStrategy;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
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

final class JooqxSQLImpl {

    static final class ReactiveSQLPQ extends SQLPQ<Tuple> implements JooqxPreparedQuery {

        protected Tuple doConvert(Map<String, Param<?>> params, DataTypeMapperRegistry registry,
                                  BiFunction<String, Param<?>, ?> queryValue) {
            final Tuple bindValues = new ArrayTuple(params.size());
            params.entrySet()
                  .stream()
                  .filter(entry -> !entry.getValue().isInline())
                  .forEachOrdered(
                      etr -> bindValues.addValue(registry.toDatabaseType(etr.getKey(), etr.getValue(), queryValue)));
            return bindValues;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public @NotNull Tuple routineValues(@NotNull Routine routine, @NotNull DataTypeMapperRegistry registry) {
            final List<Parameter<?>> inParams = routine.getInParameters();
            return inParams.stream()
                           .collect(() -> new ArrayTuple(inParams.size()), (t, p) -> t.addValue(
                               registry.toDatabaseType(p.getName(), (Param) routine.getInValue(p),
                                                       (s, param) -> param.getValue())), (t1, t2) -> { });
        }

    }


    static class ReactiveSQLRC implements JooqxResultCollector {

        @Override
        public <ROW, RESULT> @NotNull RESULT collect(@NotNull RowSet<Row> resultSet,
                                                     @NotNull SQLResultAdapter<ROW, RESULT> adapter,
                                                     @NotNull DSLContext dslContext,
                                                     @NotNull DataTypeMapperRegistry registry) {
            return adapter.collect(
                collect(resultSet, dslContext, registry, adapter.recordFactory(), adapter.strategy()));
        }

        @NotNull
        protected final <ROW> List<ROW> collect(@NotNull RowSet<Row> resultSet, @NotNull DSLContext dsl,
                                                @NotNull DataTypeMapperRegistry registry,
                                                @NotNull RecordFactory<? extends Record, ROW> recordFactory,
                                                @NotNull SelectStrategy strategy) {
            final List<ROW> records = new ArrayList<>();
            final RowIterator<Row> iterator = resultSet.iterator();
            final Function<Row, ROW> fn = row -> IntStream.range(0, row.size())
                                                          .mapToObj(i -> recordFactory.lookup(row.getColumnName(i), i))
                                                          .filter(Objects::nonNull)
                                                          .collect(rowToRecord(row, dsl, registry, recordFactory));
            if (strategy == SelectStrategy.MANY) {
                iterator.forEachRemaining(row -> records.add(fn.apply(row)));
            } else if (iterator.hasNext()) {
                final Row row = iterator.next();
                if (iterator.hasNext()) {
                    throw new TooManyRowsException();
                }
                records.add(fn.apply(row));
            }
            return records;
        }

        private <REC extends Record, ROW> Collector<FieldWrapper, REC, ROW> rowToRecord(@NotNull Row row,
                                                                                        @NotNull DSLContext dsl,
                                                                                        @NotNull DataTypeMapperRegistry registry,
                                                                                        RecordFactory<REC, ROW> recordFactory) {
            BiFunction<FieldWrapper, Row, Object> getValue = (f, r) -> {
                try {
                    return r.getValue(f.field().getName());
                } catch (NoSuchElementException e) {
                    return r.getValue(f.colNo());
                }
            };
            return Collector.of(() -> recordFactory.create(dsl),
                                (rec, f) -> rec.set(f.field(), registry.toUserType(f.field(), getValue.apply(f, row))),
                                (rec1, rec2) -> rec2, recordFactory::map);
        }

    }


    static final class ReactiveSQLBC extends ReactiveSQLRC implements JooqxBatchCollector {

        @Override
        public <ROW, RESULT> @NotNull RESULT collect(@NotNull RowSet<Row> batchResult,
                                                     @NotNull SQLResultAdapter<ROW, RESULT> adapter,
                                                     @NotNull DSLContext dslContext,
                                                     @NotNull DataTypeMapperRegistry registry) {
            final List<ROW> records = new ArrayList<>();
            while (batchResult != null) {
                final List<ROW> rows = collect(batchResult, dslContext, registry, adapter.recordFactory(),
                                               SelectStrategy.FIRST_ONE);
                if (!rows.isEmpty()) {
                    records.add(rows.get(0));
                }
                batchResult = batchResult.next();
            }
            return adapter.collect(records);
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
            if (Utils.isSpecificClient("io.vertx.pgclient.PgPool", sqlClient()) ||
                Utils.isSpecificClient("io.vertx.pgclient.PgConnection", sqlClient())) {
                if (settings.getParamType() != ParamType.INDEXED) {
                    return;
                }
                settings.setParamType(ParamType.NAMED);
            }
        }

        @Override
        public <T, R> Future<@Nullable R> execute(@NotNull Query query, @NotNull SQLResultAdapter<T, R> adapter) {
            return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                              .execute(preparedQuery().bindValues(query, typeMapperRegistry()))
                              .map(rs -> resultCollector().collect(rs, adapter, dsl(), typeMapperRegistry()))
                              .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public Future<BatchResult> batch(@NotNull Query query, @NotNull BindBatchValues bindBatchValues) {
            return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                              .executeBatch(preparedQuery().bindValues(query, bindBatchValues, typeMapperRegistry()))
                              .map(r -> new ReactiveSQLBC().batchResultSize(r))
                              .map(s -> BatchResult.create(bindBatchValues.size(), s))
                              .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public <T, R> Future<BatchReturningResult<R>> batchResult(@NotNull Query query,
                                                                  @NotNull BindBatchValues bindBatchValues,
                                                                  @NotNull SQLResultListAdapter<T, R> adapter) {
            return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                              .executeBatch(preparedQuery().bindValues(query, bindBatchValues, typeMapperRegistry()))
                              .map(rs -> new ReactiveSQLBC().collect(rs, adapter, dsl(), typeMapperRegistry()))
                              .map(rs -> BatchReturningResult.create(bindBatchValues.size(), rs))
                              .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public Future<Integer> ddl(@NotNull DDLQuery statement) {
            return sqlClient().query(preparedQuery().sql(dsl().configuration(), statement))
                              .execute()
                              .map(SqlResult::rowCount)
                              .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public Future<Integer> sql(@NotNull String statement) {
            return sqlClient().query(preparedQuery().sql(dsl().configuration(), DSL.query(statement)))
                              .execute()
                              .map(SqlResult::rowCount)
                              .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public <T, R> Future<@Nullable R> sqlQuery(@NotNull String statement, @NotNull SQLResultAdapter<T, R> adapter) {
            return execute(dsl -> dsl.resultQuery(statement), adapter);
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