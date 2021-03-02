package io.github.zero88.jooq.vertx.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import io.github.zero88.utils.Reflections.ReflectionClass;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

/**
 * Bug <a href="vertx-sql-client#909">https://github.com/eclipse-vertx/vertx-sql-client/issues/909</a>
 *
 * @param <R> Type of Jooq Record
 * @param <T> Type of Jooq Table
 * @see Record
 * @see Table
 */
public class RowSetConverter<R extends Record, T extends Table<R>> implements ResultSetConverter<RowSet<Row>, R, T> {

    @NonNull
    @Getter
    @Accessors(fluent = true)
    private final T table;
    @NonNull
    private final Class<R> recordClass;
    @NonNull
    @Getter
    private final Map<String, Field> fieldMap;

    public RowSetConverter(@NonNull T table, @NonNull Class<R> recordClass) {
        this.table = table;
        this.recordClass = recordClass;
        this.fieldMap = table.fieldStream().collect(Collectors.toMap(Field::getName, Function.identity()));
    }

    public List<R> convert(@NonNull RowSet<Row> resultSet) {
        final List<R> records = new ArrayList<>();
        resultSet.iterator().forEachRemaining(row -> {
            R record = ReflectionClass.createObject(recordClass);
            fieldMap.forEach((k, v) -> record.set(v, row.get(v.getType(), k)));
            records.add(record);
        });
        return records;
    }

}
