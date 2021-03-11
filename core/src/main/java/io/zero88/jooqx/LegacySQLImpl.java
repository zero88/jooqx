package io.zero88.jooqx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jooq.Configuration;
import org.jooq.Field;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.conf.ParamType;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.SQLOperations;
import io.zero88.jooqx.MiscImpl.BatchResultImpl;
import io.zero88.jooqx.MiscImpl.DSLAI;
import io.zero88.jooqx.SQLImpl.SQLEI;
import io.zero88.jooqx.SQLImpl.SQLPQ;
import io.zero88.jooqx.SQLImpl.SQLRSC;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.adapter.SelectStrategy;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

final class LegacySQLImpl {

    static final class LegacySQLPQ extends SQLPQ<JsonArray> implements LegacySQLPreparedQuery {

        public String sql(@NonNull Configuration configuration, @NonNull Query query) {
            return sql(configuration, query, ParamType.INDEXED);
        }

        @Override
        protected JsonArray doConvert(Map<String, Param<?>> params, BiFunction<String, Param<?>, ?> queryValue) {
            JsonArray array = new JsonArray();
            params.entrySet()
                  .stream()
                  .filter(entry -> !entry.getValue().isInline())
                  .forEachOrdered(etr -> array.add(toDatabaseType(etr.getKey(), etr.getValue(), queryValue)));
            return array;
        }

    }


    static final class LegacySQLRSC extends SQLRSC<ResultSet> implements LegacySQLResultConverter {

        protected <T extends TableLike<? extends Record>, R> List<R> doConvert(ResultSet rs, T table,
                                                                               @NonNull Function<JsonRecord<?>, R> mapper) {
            final Map<String, Field<?>> fieldMap = table.fieldStream()
                                                        .collect(Collectors.toMap(Field::getName, Function.identity()));
            final Map<Integer, Field<?>> map = getColumnMap(rs, fieldMap::get);
            final List<JsonArray> results = rs.getResults();
            if (strategy == SelectStrategy.MANY) {
                return results.stream().map(row -> toRecord(table, map, row)).map(mapper).collect(Collectors.toList());
            } else {
                warnManyResult(results.size() > 1);
                return results.stream()
                              .findFirst()
                              .map(row -> toRecord(table, map, row))
                              .map(mapper)
                              .map(Collections::singletonList)
                              .orElse(new ArrayList<>());
            }
        }

        @SuppressWarnings( {"unchecked", "rawtypes"})
        private <T extends TableLike<? extends Record>> JsonRecord<?> toRecord(T table, Map<Integer, Field<?>> map,
                                                                               JsonArray row) {
            JsonRecord<?> record = JsonRecord.create((Table<JsonRecord>) table);
            map.forEach((k, v) -> record.set((Field<Object>) v, v.getType().cast(row.getValue(k))));
            return record;
        }

        private Map<Integer, Field<?>> getColumnMap(ResultSet rs, Function<String, Field<?>> lookupField) {
            return IntStream.range(0, rs.getNumColumns())
                            .boxed()
                            .collect(Collectors.toMap(i -> i, i -> lookupField.apply(rs.getColumnNames().get(i))));
        }

        @Override
        public int batchResultSize(List<Integer> batchResult) {
            return batchResult.size();
        }

    }


    static final class LegacyDSLAI extends DSLAI<ResultSet, LegacySQLResultConverter> implements LegacyDSL {

        LegacyDSLAI(@NonNull LegacySQLResultConverter converter) {
            super(converter);
        }

        LegacyDSLAI() {
            super(new LegacySQLRSC());
        }

    }


    @Getter
    @SuperBuilder
    @Accessors(fluent = true)
    abstract static class LegacySQLEI<S extends SQLOperations> extends SQLEI<S, JsonArray, ResultSet> {

        @Default
        @NonNull
        private final SQLPreparedQuery<JsonArray> preparedQuery = new LegacySQLPQ();

        @Override
        public final <Q extends Query, T extends TableLike<?>, C extends SQLResultSetConverter<ResultSet>, R> Future<R> execute(
            @NonNull Q query, @NonNull SQLResultAdapter<ResultSet, C, T, R> adapter) {
            final Promise<ResultSet> promise = Promise.promise();
            sqlClient().queryWithParams(preparedQuery().sql(dsl().configuration(), query),
                                        preparedQuery().bindValues(query), promise);
            return promise.future().map(adapter::convert).otherwise(errorConverter()::reThrowError);
        }

        @Override
        public final <Q extends Query> Future<BatchResult> batch(@NonNull Q query,
                                                                 @NonNull BindBatchValues bindBatchValues) {
            final Promise<List<Integer>> promise = Promise.promise();
            openConn().map(c -> c.batchWithParams(preparedQuery().sql(dsl().configuration(), query),
                                                  preparedQuery().bindValues(query, bindBatchValues), promise));
            return promise.future()
                          .map(r -> new LegacySQLRSC().batchResultSize(r))
                          .map(s -> BatchResultImpl.create(bindBatchValues.size(), s))
                          .otherwise(errorConverter()::reThrowError);
        }

        protected abstract Future<SQLConnection> openConn();

    }

}
