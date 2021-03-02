package io.github.zero88.jooq.vertx.converter;

import java.util.List;
import java.util.Map;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import io.vertx.ext.sql.ResultSet;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@RequiredArgsConstructor
public class LegacyResultSetConverter<R extends Record, T extends Table<R>>
    implements ResultSetConverter<ResultSet, R, T> {

    @NonNull
    @Getter
    @Accessors(fluent = true)
    private final T table;
    @NonNull
    private final Class<R> recordClass;
    @NonNull
    @Getter
    private final Map<String, Field> fieldMap;

    @Override
    public List<R> convert(@NonNull ResultSet resultSet) {
        return null;
    }

}
