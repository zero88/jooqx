package io.zero88.jooqx;

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

import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.TableLike;
import org.jooq.conf.ParamType;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.SQLOperations;
import io.zero88.jooqx.MiscImpl.BatchResultImpl;
import io.zero88.jooqx.MiscImpl.DSLAI;
import io.zero88.jooqx.SQLImpl.SQLEI;
import io.zero88.jooqx.SQLImpl.SQLPQ;
import io.zero88.jooqx.SQLImpl.SQLRC;
import io.zero88.jooqx.adapter.RowConverterStrategy;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.adapter.SelectStrategy;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

final class LegacySQLImpl {

    interface LegacyInternal<S extends SQLOperations> extends SQLExecutor<S, JsonArray, ResultSet, LegacySQLCollector> {

        @Override
        @NonNull LegacySQLPreparedQuery preparedQuery();

        @Override
        <T extends TableLike<?>, R> Future<R> execute(@NonNull Query query,
                                                      @NonNull SQLResultAdapter<ResultSet, LegacySQLCollector, T, R> adapter);

    }


    static final class LegacySQLPQ extends SQLPQ<JsonArray> implements LegacySQLPreparedQuery {

        public String sql(@NonNull Configuration configuration, @NonNull Query query) {
            return sql(configuration, query, ParamType.INDEXED);
        }

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


    static final class LegacySQLRC extends SQLRC<ResultSet> implements LegacySQLCollector {

        @Override
        public @NonNull <R extends Record, O> List<O> collect(@NonNull ResultSet resultSet,
                                                              @NonNull RowConverterStrategy<R, O> strategy) {
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

        private <R extends Record, O> O toRecord(RowConverterStrategy<R, O> strategy, Map<Field<?>, Integer> map,
                                                 JsonArray row) {
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
        public int batchResultSize(List<Integer> batchResult) {
            return batchResult.size();
        }

    }


    static final class LegacyDSLAdapter extends DSLAI<ResultSet, LegacySQLCollector> implements LegacyDSL {

        LegacyDSLAdapter() {
            super(new LegacySQLRC());
        }

    }


    @Getter
    @SuperBuilder
    @Accessors(fluent = true)
    abstract static class LegacySQLEI<S extends SQLOperations>
        extends SQLEI<S, JsonArray, ResultSet, LegacySQLCollector> implements LegacyInternal<S> {

        @Default
        @NonNull
        private final LegacySQLPreparedQuery preparedQuery = new LegacySQLPQ();

        @Override
        public final <T extends TableLike<?>, R> Future<R> execute(@NonNull Query query,
                                                                   @NonNull SQLResultAdapter<ResultSet,
                                                                                                LegacySQLCollector, T
                                                                                                , R> adapter) {
            final Promise<ResultSet> promise = Promise.promise();
            sqlClient().queryWithParams(preparedQuery().sql(dsl().configuration(), query),
                                        preparedQuery().bindValues(query, typeMapperRegistry()), promise);
            return promise.future()
                          .map(rs -> adapter.collect(rs, dsl(), typeMapperRegistry()))
                          .otherwise(errorConverter()::reThrowError);
        }

        @Override
        public final Future<BatchResult> batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues) {
            final Promise<List<Integer>> promise = Promise.promise();
            openConn().map(c -> c.batchWithParams(preparedQuery().sql(dsl().configuration(), query),
                                                  preparedQuery().bindValues(query, bindBatchValues,
                                                                             typeMapperRegistry()), promise));
            return promise.future()
                          .map(r -> new LegacySQLRC().batchResultSize(r))
                          .map(s -> BatchResultImpl.create(bindBatchValues.size(), s))
                          .otherwise(errorConverter()::reThrowError);
        }

        protected abstract Future<SQLConnection> openConn();

    }


    @Getter
    @SuperBuilder
    @Accessors(fluent = true)
    static final class LegacyJooqxImpl extends LegacySQLEI<SQLClient> implements LegacyJooqx {

        @Override
        @SuppressWarnings("unchecked")
        public @NonNull LegacyJooqxTx transaction() {
            return LegacySQLImpl.LegacyJooqTxImpl.builder()
                                                 .vertx(vertx())
                                                 .dsl(dsl())
                                                 .preparedQuery(preparedQuery())
                                                 .errorConverter(errorConverter())
                                                 .delegate(this)
                                                 .build();
        }

        @Override
        protected LegacyJooqxImpl withSqlClient(@NonNull SQLClient sqlClient) {
            throw new UnsupportedOperationException("No need");
        }

        protected Future<SQLConnection> openConn() {
            final Promise<SQLConnection> promise = Promise.promise();
            sqlClient().getConnection(ar -> {
                if (ar.failed()) {
                    promise.fail(connFailed("Unable open SQL connection", ar.cause()));
                } else {
                    promise.complete(ar.result());
                }
            });
            return promise.future();
        }

    }


    @Getter
    @SuperBuilder
    @Accessors(fluent = true)
    static final class LegacyJooqTxImpl extends LegacySQLEI<SQLConnection> implements LegacyJooqxTx {

        private final LegacySQLEI<SQLClient> delegate;

        @Override
        public <X> Future<X> run(@NonNull Function<LegacyJooqxTx, Future<X>> block) {
            final Promise<X> promise = Promise.promise();
            delegate.openConn().map(conn -> conn.setAutoCommit(false, committable -> {
                if (committable.failed()) {
                    failed(conn, promise, connFailed("Unable begin transaction", committable.cause()));
                } else {
                    block.apply(withSqlClient(conn))
                         .onSuccess(r -> commit(conn, promise, r))
                         .onFailure(t -> rollback(conn, promise, t));
                }
            }));
            return promise.future();
        }

        @Override
        protected Future<SQLConnection> openConn() {
            return Future.succeededFuture(sqlClient());
        }

        @Override
        protected LegacyJooqTxImpl withSqlClient(@NonNull SQLConnection sqlConn) {
            return LegacyJooqTxImpl.builder()
                                   .vertx(vertx())
                                   .sqlClient(sqlConn)
                                   .dsl(dsl())
                                   .preparedQuery(preparedQuery())
                                   .errorConverter(errorConverter())
                                   .build();
        }

        private <X> void commit(@NonNull SQLConnection conn, @NonNull Promise<X> promise, X output) {
            conn.commit(v -> {
                if (v.succeeded()) {
                    promise.complete(output);
                    conn.close();
                } else {
                    rollback(conn, promise, errorConverter().handle(v.cause()));
                }
            });
        }

        private <X> void rollback(@NonNull SQLConnection conn, @NonNull Promise<X> promise, @NonNull Throwable t) {
            conn.rollback(rb -> {
                if (!rb.succeeded()) {
                    t.addSuppressed(rb.cause());
                }
                failed(conn, promise, t);
            });
        }

        private <X> void failed(@NonNull SQLConnection conn, @NonNull Promise<X> promise, @NonNull Throwable t) {
            promise.fail(t);
            conn.close();
        }

    }

}
