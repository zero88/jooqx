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

import io.github.zero88.jooq.vertx.record.VertxJooqRecord;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;

import lombok.NonNull;

public class LegacyResultSetConverter<T extends TableLike<? extends Record>>
    extends AbstractResultSetConverter<ResultSet, T>
    implements ResultSetConverter<ResultSet, T>, ResultSetBatchConverter<ResultSet, T> {

    public LegacyResultSetConverter(@NonNull T table) {
        super(table);
    }

    protected <R> List<R> doConvert(ResultSet resultSet, @NonNull Function<VertxJooqRecord<?>, R> mapper) {
        final Map<Integer, Field<?>> map = getColumnMap(resultSet);
        return resultSet.getResults().stream().map(row -> toRecord(map, row)).map(mapper).collect(Collectors.toList());
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    private VertxJooqRecord<?> toRecord(Map<Integer, Field<?>> map, JsonArray row) {
        VertxJooqRecord<?> record = VertxJooqRecord.create((Table<VertxJooqRecord>) table());
        map.forEach((k, v) -> record.set((Field<Object>) v, v.getType().cast(row.getValue(k))));
        return record;
    }

    private Map<Integer, Field<?>> getColumnMap(ResultSet resultSet) {
        return IntStream.range(0, resultSet.getNumColumns())
                        .boxed()
                        .collect(Collectors.toMap(i -> i, i -> lookupField(resultSet.getColumnNames().get(i))));
    }

}
