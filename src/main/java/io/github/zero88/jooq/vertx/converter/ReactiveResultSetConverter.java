package io.github.zero88.jooq.vertx.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.VertxJooqRecord;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import lombok.NonNull;

/**
 * Bug <a href="vertx-sql-client#909">https://github.com/eclipse-vertx/vertx-sql-client/issues/909</a>
 *
 * @see Record
 * @see TableLike
 * @see ResultSetConverter
 */
public class ReactiveResultSetConverter extends AbstractResultSetConverter<RowSet<Row>> {

    @Override
    protected <T extends TableLike<? extends Record>, R> List<R> doConvert(@NonNull RowSet<Row> resultSet, T table,
                                                                           @NonNull Function<VertxJooqRecord<?>, R> mapper) {
        final Map<String, Field<?>> fieldMap = table.fieldStream()
                                                    .collect(Collectors.toMap(Field::getName, Function.identity()));
        final List<R> records = new ArrayList<>();
        resultSet.iterator().forEachRemaining(row -> records.add(mapper.apply(toRecord(table, row, fieldMap::get))));
        return records;
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    private VertxJooqRecord<?> toRecord(@NonNull TableLike table, @NonNull Row row,
                                        @NonNull Function<String, Field<?>> lookupField) {
        VertxJooqRecord<?> record = VertxJooqRecord.create((Table<VertxJooqRecord>) table);
        IntStream.range(0, row.size())
                 .mapToObj(row::getColumnName)
                 .map(lookupField)
                 .filter(Objects::nonNull)
                 .forEach(f -> record.set((Field<Object>) f, row.get(f.getType(), f.getName())));
        return record;
    }

}
