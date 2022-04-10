package io.github.zero88.jooqx;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.jooq.DDLQuery;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.Query;

import io.github.zero88.jooqx.MiscImpl.BatchResultImpl;
import io.github.zero88.jooqx.SQLImpl.SQLEI;
import io.github.zero88.jooqx.SQLImpl.SQLExecutorBuilderImpl;
import io.github.zero88.jooqx.SQLImpl.SQLPQ;
import io.github.zero88.jooqx.adapter.RowConverterStrategy;
import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.github.zero88.jooqx.adapter.SelectStrategy;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.SQLOperations;
import io.vertx.ext.sql.UpdateResult;

final class LegacySQLImpl {

    interface LegacyInternal<S extends SQLOperations>
        extends SQLExecutor<S, JsonArray, LegacySQLPreparedQuery, ResultSet, LegacySQLCollector> {

        @Override
        @NotNull LegacySQLPreparedQuery preparedQuery();

        @Override
        @NotNull LegacySQLCollector resultCollector();

    }


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

    }


    static final class LegacySQLRC implements LegacySQLCollector {

        @NotNull
        @Override
        public <T, R> List<R> collect(@NotNull ResultSet resultSet, @NotNull RowConverterStrategy<T, R> strategy) {
            final Map<Field<?>, Integer> map = getColumnMap(resultSet, strategy::lookupField);
            final List<JsonArray> results = resultSet.getResults();
            if (strategy.strategy() == SelectStrategy.MANY) {
                return results.stream().map(row -> toRecord(strategy, map, row)).collect(Collectors.toList());
            }
            warnManyResult(results.size() > 1, strategy.strategy());
            return results.stream()
                          .findFirst()
                          .map(row -> toRecord(strategy, map, row))
                          .map(Collections::singletonList)
                          .orElse(new ArrayList<>());
        }

        private <T, R> R toRecord(RowConverterStrategy<T, R> strategy, Map<Field<?>, Integer> map, JsonArray row) {
            return map.keySet().stream().collect(strategy.createCollector(f -> row.getValue(map.get(f))));
        }

        private Map<Field<?>, Integer> getColumnMap(ResultSet rs, Function<String, Field<?>> lookupField) {
            return IntStream.range(0, rs.getNumColumns())
                            .boxed()
                            .map(i -> Optional.ofNullable(lookupField.apply(rs.getColumnNames().get(i)))
                                              .map(f -> new SimpleEntry<>(f, i))
                                              .orElse(null))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        }

        @Override
        public int batchResultSize(@NotNull List<Integer> batchResult) { return batchResult.size(); }

    }


    abstract static class LegacySQLEI<S extends SQLOperations>
        extends SQLEI<S, JsonArray, LegacySQLPreparedQuery, ResultSet, LegacySQLCollector>
        implements LegacyInternal<S> {

        LegacySQLEI(Vertx vertx, DSLContext dsl, S sqlClient, LegacySQLPreparedQuery preparedQuery,
                    LegacySQLCollector resultCollector, SQLErrorConverter errorConverter,
                    DataTypeMapperRegistry typeMapperRegistry) {
            super(vertx, dsl, sqlClient, preparedQuery, resultCollector, errorConverter, typeMapperRegistry);
        }

        @Override
        public final <T, R> Future<R> execute(@NotNull Query query, @NotNull SQLResultAdapter<T, R> adapter) {
            final Promise<ResultSet> promise = Promise.promise();
            sqlClient().queryWithParams(preparedQuery().sql(dsl().configuration(), query),
                                        preparedQuery().bindValues(query, typeMapperRegistry()), promise);
            return promise.future()
                          .map(rs -> adapter.collect(rs, resultCollector(), dsl(), typeMapperRegistry()))
                          .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public final Future<BatchResult> batch(@NotNull Query query, @NotNull BindBatchValues bindBatchValues) {
            final Promise<List<Integer>> promise = Promise.promise();
            openConn().map(c -> c.batchWithParams(preparedQuery().sql(dsl().configuration(), query),
                                                  preparedQuery().bindValues(query, bindBatchValues,
                                                                             typeMapperRegistry()), promise));
            return promise.future()
                          .map(r -> resultCollector().batchResultSize(r))
                          .map(s -> BatchResultImpl.create(bindBatchValues.size(), s))
                          .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public Future<Integer> ddl(@NotNull DDLQuery statement) {
            final Promise<UpdateResult> promise = Promise.promise();
            sqlClient().update(preparedQuery().sql(dsl().configuration(), statement), promise);
            return promise.future().map(UpdateResult::getUpdated).otherwise(errorConverter()::reThrowError);
        }

        protected abstract Future<SQLConnection> openConn();

        @Override
        @NotNull
        protected final LegacySQLPreparedQuery defPrepareQuery() { return LegacySQLPreparedQuery.create(); }

        @Override
        @NotNull
        protected final LegacySQLCollector defResultCollector() { return LegacySQLCollector.create(); }

    }


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


    static class LegacyJooqxBuilderImpl extends SQLExecutorBuilderImpl<SQLClient, JsonArray, LegacySQLPreparedQuery,
                                                                      ResultSet, LegacySQLCollector, LegacyJooqxBuilder>
        implements LegacyJooqxBuilder {

        @Override
        public @NotNull LegacyJooqx build() {
            return new LegacyJooqxImpl(vertx(), dsl(), sqlClient(), preparedQuery(), resultCollector(),
                                       errorConverter(), typeMapperRegistry());
        }

    }

}
