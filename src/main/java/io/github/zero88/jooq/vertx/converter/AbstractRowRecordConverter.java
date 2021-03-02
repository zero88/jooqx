package io.github.zero88.jooq.vertx.converter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.TableLike;

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
    public Field fieldMapper(String field) {
        return fieldMap.get(field);
    }

}
