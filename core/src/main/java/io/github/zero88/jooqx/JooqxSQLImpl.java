package io.github.zero88.jooqx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
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
        public @NotNull <ROW, RESULT> Collector<Row, List<ROW>, RESULT> collector(
            @NotNull SQLResultAdapter<ROW, RESULT> adapter, @NotNull DSLContext dsl,
            @NotNull DataTypeMapperRegistry registry) {
            return Collector.of(ArrayList::new,
                                (rows, row) -> rows.add(rowToRecord(row, dsl, registry, adapter.recordFactory())),
                                (rows1, rows2) -> rows2, rows -> collect(adapter, rows));
        }

        protected final <REC extends Record, ROW> ROW rowToRecord(@NotNull Row row, @NotNull DSLContext dsl,
                                                                  @NotNull DataTypeMapperRegistry registry,
                                                                  @NotNull RecordFactory<REC, ROW> recordFactory) {
            final Function<FieldWrapper, Object> getValue = (f) -> {
                try {
                    return registry.toUserType(f.field(), row.getValue(f.field().getName()));
                } catch (NoSuchElementException e) {
                    return registry.toUserType(f.field(), row.getValue(f.colNo()));
                }
            };
            final BiConsumer<REC, FieldWrapper> accumulator = (rec, f) -> rec.set(f.field(), getValue.apply(f));
            return IntStream.range(0, row.size())
                            .mapToObj(i -> recordFactory.lookup(row.getColumnName(i), i))
                            .filter(Objects::nonNull)
                            .collect(Collector.of(() -> recordFactory.create(dsl), accumulator, (r1, r2) -> r2,
                                                  recordFactory::map));
        }

        private static <ROW, RESULT> RESULT collect(@NotNull SQLResultAdapter<ROW, RESULT> adapter, List<ROW> rows) {
            if (adapter.strategy() == SelectStrategy.FIRST_ONE && rows.size() > 1) {
                throw new TooManyRowsException();
            }
            return adapter.collect(rows);
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
                              .collecting(resultCollector().collector(adapter, dsl(), typeMapperRegistry()))
                              .execute(preparedQuery().bindValues(query, typeMapperRegistry()))
                              .map(SqlResult::value)
                              .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public Future<BatchResult> batch(@NotNull Query query, @NotNull BindBatchValues bindBatchValues) {
            return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                              .collecting(Collectors.toList())
                              .executeBatch(preparedQuery().bindValues(query, bindBatchValues, typeMapperRegistry()))
                              .map(resultCollector().<Row>batchCollector()::batchResultSize)
                              .map(s -> BatchResult.create(bindBatchValues.size(), s))
                              .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public <T, R> Future<BatchReturningResult<R>> batchResult(@NotNull Query query,
                                                                  @NotNull BindBatchValues bindBatchValues,
                                                                  @NotNull SQLResultListAdapter<T, R> adapter) {
            return sqlClient().preparedQuery(preparedQuery().sql(dsl().configuration(), query))
                              .collecting(resultCollector().collector(adapter, dsl(), typeMapperRegistry()))
                              .executeBatch(preparedQuery().bindValues(query, bindBatchValues, typeMapperRegistry()))
                              .map(resultCollector().<R>batchCollector()::reduce)
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
