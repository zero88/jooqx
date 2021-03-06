package io.github.zero88.jooq.vertx.converter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.VertxJooqRecord;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;

import lombok.NonNull;

public class LegacyResultSetConverter extends AbstractResultSetConverter<ResultSet>
    implements ResultSetConverter<ResultSet>, ResultBatchConverter<ResultSet> {

    protected <T extends TableLike<? extends Record>, R> List<R> doConvert(ResultSet resultSet, T table,
                                                                           @NonNull Function<VertxJooqRecord<?>, R> mapper) {
        final Map<String, Field<?>> fieldMap = table.fieldStream()
                                                    .collect(Collectors.toMap(Field::getName, Function.identity()));
        final Map<Integer, Field<?>> map = getColumnMap(resultSet, fieldMap::get);
        return resultSet.getResults()
                        .stream()
                        .map(row -> toRecord(table, map, row))
                        .map(mapper)
                        .collect(Collectors.toList());
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    private <T extends TableLike<? extends Record>> VertxJooqRecord<?> toRecord(T table, Map<Integer, Field<?>> map,
                                                                                JsonArray row) {
        VertxJooqRecord<?> record = VertxJooqRecord.create((Table<VertxJooqRecord>) table);
        map.forEach((k, v) -> record.set((Field<Object>) v, v.getType().cast(row.getValue(k))));
        return record;
    }

    private Map<Integer, Field<?>> getColumnMap(ResultSet resultSet, Function<String, Field<?>> lookupField) {
        return IntStream.range(0, resultSet.getNumColumns())
                        .boxed()
                        .collect(Collectors.toMap(i -> i, i -> lookupField.apply(resultSet.getColumnNames().get(i))));
    }

}
