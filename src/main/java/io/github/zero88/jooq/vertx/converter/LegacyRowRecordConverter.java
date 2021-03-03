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

public class LegacyRowRecordConverter<T extends TableLike<? extends Record>>
    extends AbstractRowRecordConverter<ResultSet, T> implements ResultSetConverter<ResultSet, T> {

    public LegacyRowRecordConverter(@NonNull T table) {
        super(table);
    }

    protected  <R> List<R> doConvert(ResultSet resultSet, Function<VertxJooqRecord<?>, R> mapper) {
        final Map<Integer, Field> map = getColumnMap(resultSet);
        return resultSet.getResults().stream().map(row -> mapper(map, row)).map(mapper).collect(Collectors.toList());
    }

    private VertxJooqRecord<?> mapper(Map<Integer, Field> map, JsonArray row) {
        VertxJooqRecord<?> record = new VertxJooqRecord<>((Table<VertxJooqRecord>) table());
        map.forEach((k, v) -> record.set(v, row.getValue(k)));
        return record;
    }

    private Map<Integer, Field> getColumnMap(ResultSet resultSet) {
        return IntStream.range(0, resultSet.getNumColumns())
                        .boxed()
                        .collect(Collectors.toMap(i -> i, i -> lookupField(resultSet.getColumnNames().get(i))));
    }

}
