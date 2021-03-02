package io.github.zero88.jooq.vertx.converter;

import java.util.List;
import java.util.Map;
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

    @Override
    public List<VertxJooqRecord<?>> convert(@NonNull ResultSet resultSet) {
        final Map<Integer, Field> map = IntStream.range(0, resultSet.getNumColumns())
                                                 .boxed()
                                                 .collect(Collectors.toMap(i -> i, i -> fieldMapper(
                                                     resultSet.getColumnNames().get(i))));
        return resultSet.getResults().stream().map(row -> toRecord(map, row)).collect(Collectors.toList());
    }

    private VertxJooqRecord<?> toRecord(Map<Integer, Field> map, JsonArray row) {
        VertxJooqRecord<?> record = new VertxJooqRecord<>((Table<VertxJooqRecord>) table());
        map.forEach((k, v) -> record.set(v, row.getValue(k)));
        return record;
    }

}
