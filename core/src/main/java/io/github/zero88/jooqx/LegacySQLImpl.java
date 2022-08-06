package io.github.zero88.jooqx;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;

import io.github.zero88.jooqx.SQLImpl.SQLEI;
import io.github.zero88.jooqx.SQLImpl.SQLExecutorBuilderImpl;
import io.github.zero88.jooqx.SQLImpl.SQLPQ;
import io.github.zero88.jooqx.adapter.FieldWrapper;
import io.github.zero88.jooqx.adapter.RecordFactory;
import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.github.zero88.jooqx.adapter.SelectStrategy;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.SQLOperations;
import io.vertx.ext.sql.UpdateResult;

@Deprecated
final class LegacySQLImpl {

    @Deprecated
    static final class LegacySQLPQ extends SQLPQ<JsonArray> implements LegacySQLPreparedQuery {

        @Override
        protected JsonArray doConvert(Map<String, Param<?>> params, DataTypeMapperRegistry registry,
                                      BiFunction<String, Param<?>, ?> queryValue) {
            JsonArray array = new JsonArray();
            params.entrySet()
                  .stream()
                  .filter(entry -> !entry.getValue().isInline())
                  .forEachOrdered(etr -> array.add(registry.toDatabaseType(etr.getKey(), etr.getValue(), queryValue)));
            return array;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public @NotNull JsonArray routineValues(@NotNull Routine routine, @NotNull DataTypeMapperRegistry registry) {
            final List<Parameter<?>> inParams = routine.getInParameters();
            return inParams.stream()
                           .collect(JsonArray::new, (t, p) -> t.add(
                               registry.toDatabaseType(p.getName(), (Param<?>) routine.getInValue(p),
                                                       (s, param) -> param.getValue())), (t1, t2) -> { });
        }

    }


    @Deprecated
    static final class LegacySQLRC implements LegacySQLCollector {

        @Override
        public <ROW, RESULT> @Nullable RESULT collect(@NotNull ResultSet resultSet,
                                                      @NotNull SQLResultAdapter<ROW, RESULT> adapter,
                                                      @NotNull DSLContext dsl,
                                                      @NotNull DataTypeMapperRegistry registry) {
            final RecordFactory<? extends Record, ROW> recordFactory = adapter.recordFactory();
            final List<FieldWrapper> fields = getColumns(resultSet.getColumnNames(), recordFactory::lookup);
            final List<JsonArray> results = resultSet.getResults();
            if (adapter.strategy() == SelectStrategy.MANY) {
                return adapter.collect(results.stream()
                                              .map(row -> fields.stream()
                                                                .collect(collector(row, dsl, registry, recordFactory)))
                                              .collect(Collectors.toList()));
            }
            if (results.size() > 1) {
                throw new TooManyRowsException();
            }
            return results.stream()
                          .findFirst()
                          .map(row -> fields.stream().collect(collector(row, dsl, registry, recordFactory)))
                          .map(Collections::singletonList)
                          .map(adapter::collect)
                          .orElse(null);
        }

        @NotNull
        private <REC extends Record, ROW> Collector<FieldWrapper, REC, ROW> collector(JsonArray row, DSLContext dsl,
                                                                                      DataTypeMapperRegistry registry,
                                                                                      RecordFactory<REC, ROW> recordFactory) {
            return Collector.of(() -> recordFactory.create(dsl),
                                (rec, f) -> rec.set(f.field(), registry.toUserType(f.field(), row.getValue(f.colNo()))),
                                (rec1, rec2) -> rec2, recordFactory::map);
        }

        private List<FieldWrapper> getColumns(List<String> columnNames,
                                              BiFunction<String, Integer, FieldWrapper> lookupField) {
            return IntStream.range(0, columnNames.size())
                            .mapToObj(i -> lookupField.apply(columnNames.get(i), i))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
        }

    }


    @Deprecated
    abstract static class LegacySQLEI<S extends SQLOperations>
        extends SQLEI<S, JsonArray, LegacySQLPreparedQuery, LegacySQLCollector> implements LegacyInternal<S> {

        LegacySQLEI(Vertx vertx, DSLContext dsl, S sqlClient, LegacySQLPreparedQuery preparedQuery,
                    LegacySQLCollector resultCollector, SQLErrorConverter errorConverter,
                    DataTypeMapperRegistry typeMapperRegistry) {
            super(vertx, dsl, sqlClient, preparedQuery, resultCollector, errorConverter, typeMapperRegistry);
        }

        @Override
        public final <T, R> Future<@Nullable R> execute(@NotNull Query query, @NotNull SQLResultAdapter<T, R> adapter) {
            final Promise<ResultSet> promise = Promise.promise();
            sqlClient().queryWithParams(preparedQuery().sql(dsl().configuration(), query),
                                        preparedQuery().bindValues(query, typeMapperRegistry()), promise);
            return promise.future()
                          .map(rs -> resultCollector().collect(rs, adapter, dsl(), typeMapperRegistry()))
                          .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public final Future<BatchResult> batch(@NotNull Query query, @NotNull BindBatchValues bindBatchValues) {
            final Promise<List<Integer>> promise = Promise.promise();
            openConn().map(c -> c.batchWithParams(preparedQuery().sql(dsl().configuration(), query),
                                                  preparedQuery().bindValues(query, bindBatchValues,
                                                                             typeMapperRegistry()), promise));
            return promise.future()
                          .map(r -> resultCollector().batchResult(bindBatchValues, r))
                          .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public Future<Integer> ddl(@NotNull DDLQuery statement) {
            final Promise<UpdateResult> promise = Promise.promise();
            sqlClient().update(preparedQuery().sql(dsl().configuration(), statement), promise);
            return promise.future().map(UpdateResult::getUpdated).otherwise(errorConverter()::reThrowError);
        }

        @Override
        public Future<Integer> sql(@NotNull String statement) {
            final Promise<UpdateResult> promise = Promise.promise();
            sqlClient().update(preparedQuery().sql(dsl().configuration(), DSL.query(statement)), promise);
            return promise.future().map(UpdateResult::getUpdated).otherwise(errorConverter()::reThrowError);
        }

        @Override
        public <T, R> Future<@Nullable R> sqlQuery(@NotNull String statement, @NotNull SQLResultAdapter<T, R> adapter) {
            return execute(dsl -> dsl.resultQuery(statement), adapter);
        }

        protected abstract Future<SQLConnection> openConn();

        @Override
        @NotNull
        protected final LegacySQLPreparedQuery defPrepareQuery() { return LegacySQLPreparedQuery.create(); }

        @Override
        @NotNull
        protected final LegacySQLCollector defResultCollector() { return LegacySQLCollector.create(); }

    }


    @Deprecated
    static final class LegacyJooqxImpl extends LegacySQLEI<SQLClient> implements LegacyJooqx {

        LegacyJooqxImpl(Vertx vertx, DSLContext dsl, SQLClient sqlClient, LegacySQLPreparedQuery preparedQuery,
                        LegacySQLCollector resultCollector, SQLErrorConverter errorConverter,
                        DataTypeMapperRegistry typeMapperRegistry) {
            super(vertx, dsl, sqlClient, preparedQuery, resultCollector, errorConverter, typeMapperRegistry);
        }

        @Override
        @SuppressWarnings("unchecked")
        public @NotNull LegacyJooqxTx transaction() { return new LegacyJooqTxImpl(this); }

        @Override
        protected LegacyJooqx withSqlClient(@NotNull SQLClient sqlClient) {
            throw new UnsupportedOperationException("Invalid transaction state");
        }

        protected Future<SQLConnection> openConn() {
            final Promise<SQLConnection> promise = Promise.promise();
            sqlClient().getConnection(ar -> {
                if (ar.failed()) {
                    promise.fail(transientConnFailed("Unable open SQL connection", ar.cause()));
                } else {
                    promise.complete(ar.result());
                }
            });
            return promise.future();
        }

    }


    @Deprecated
    static final class LegacyJooqTxImpl extends LegacySQLEI<SQLConnection> implements LegacyJooqxTx {

        private final LegacySQLEI<SQLClient> delegate;

        LegacyJooqTxImpl(Vertx vertx, DSLContext dsl, SQLConnection sqlClient, LegacySQLPreparedQuery preparedQuery,
                         LegacySQLCollector resultCollector, SQLErrorConverter errorConverter,
                         DataTypeMapperRegistry typeMapperRegistry) {
            super(vertx, dsl, sqlClient, preparedQuery, resultCollector, errorConverter, typeMapperRegistry);
            this.delegate = null;
        }

        LegacyJooqTxImpl(@NotNull LegacySQLEI<SQLClient> delegate) {
            super(delegate.vertx(), delegate.dsl(), null, delegate.preparedQuery(), delegate.resultCollector(),
                  delegate.errorConverter(), delegate.typeMapperRegistry());
            this.delegate = delegate;
        }

        @Override
        public <X> Future<X> run(@NotNull Function<LegacyJooqxTx, Future<X>> block) {
            final Promise<X> promise = Promise.promise();
            delegate.openConn().map(conn -> conn.setAutoCommit(false, committable -> {
                if (committable.failed()) {
                    failed(conn, promise, transientConnFailed("Unable begin transaction", committable.cause()));
                } else {
                    block.apply(withSqlClient(conn))
                         .onSuccess(r -> commit(conn, promise, r))
                         .onFailure(t -> rollback(conn, promise, t));
                }
            }));
            return promise.future();
        }

        @Override
        protected Future<SQLConnection> openConn() { return Future.succeededFuture(sqlClient()); }

        @Override
        protected LegacyJooqxTx withSqlClient(@NotNull SQLConnection sqlConn) {
            return new LegacyJooqTxImpl(vertx(), dsl(), sqlConn, preparedQuery(), resultCollector(), errorConverter(),
                                        typeMapperRegistry());
        }

        private <X> void commit(@NotNull SQLConnection conn, @NotNull Promise<X> promise, X output) {
            conn.commit(v -> {
                if (v.succeeded()) {
                    promise.complete(output);
                    conn.close();
                } else {
                    rollback(conn, promise, errorConverter().handle(v.cause()));
                }
            });
        }

        private <X> void rollback(@NotNull SQLConnection conn, @NotNull Promise<X> promise, @NotNull Throwable t) {
            conn.rollback(rb -> {
                if (!rb.succeeded()) {
                    t.addSuppressed(rb.cause());
                }
                failed(conn, promise, t);
            });
        }

        private <X> void failed(@NotNull SQLConnection conn, @NotNull Promise<X> promise, @NotNull Throwable t) {
            promise.fail(t);
            conn.close();
        }

    }


    @Deprecated
    static class LegacyJooqxBuilderImpl extends
                                        SQLExecutorBuilderImpl<SQLClient, JsonArray, LegacySQLPreparedQuery,
                                                                  ResultSet, LegacySQLCollector, LegacyJooqxBuilder>
        implements LegacyJooqxBuilder {

        @Override
        public @NotNull LegacyJooqx build() {
            return new LegacyJooqxImpl(vertx(), dsl(), sqlClient(), preparedQuery(), resultCollector(),
                                       errorConverter(), typeMapperRegistry());
        }

    }

}
