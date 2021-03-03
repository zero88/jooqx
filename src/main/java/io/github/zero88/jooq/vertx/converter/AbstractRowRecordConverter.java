package io.github.zero88.jooq.vertx.converter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.record.VertxJooqRecord;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

public abstract class AbstractRowRecordConverter<RS, T extends TableLike<? extends Record>>
    implements ResultSetConverter<RS, T> {

    @NonNull
    @Getter
    @Accessors(fluent = true)
    private final T table;
    @NonNull
    private final Map<String, Field> fieldMap;

    protected AbstractRowRecordConverter(@NonNull T table) {
        this.table = table;
        this.fieldMap = table.fieldStream().collect(Collectors.toMap(Field::getName, Function.identity()));
    }

    @Override
    public Field lookupField(String field) {
        return fieldMap.get(field);
    }

    @Override
    public List<VertxJooqRecord<?>> convert(@NonNull RS resultSet) {
        return doConvert(resultSet, Function.identity());
    }

    @Override
    public <R> List<R> convert(@NonNull RS resultSet, @NonNull Class<R> record) {
        return doConvert(resultSet, r -> r.into(record));
    }

    @Override
    public <R extends Record> List<R> convert(@NonNull RS resultSet, @NonNull Table<R> table) {
        return doConvert(resultSet, r -> r.into(table));
    }

    protected abstract <R> List<R> doConvert(@NonNull RS resultSet, @NonNull Function<VertxJooqRecord<?>, R> mapper);

}
