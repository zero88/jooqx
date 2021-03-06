package io.github.zero88.jooq.vertx.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
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
 * @param <T> Type of jOOQ Table
 * @see Record
 * @see TableLike
 * @see ResultSetConverter
 */
public class ReactiveResultSetConverter<T extends TableLike<? extends Record>>
    extends AbstractResultSetConverter<RowSet<Row>, T> implements ResultSetConverter<RowSet<Row>, T> {

    public ReactiveResultSetConverter(@NonNull T table) {
        super(table);
    }

    @Override
    protected <R> List<R> doConvert(@NonNull RowSet<Row> resultSet, @NonNull Function<VertxJooqRecord<?>, R> mapper) {
        final List<R> records = new ArrayList<>();
        resultSet.iterator().forEachRemaining(row -> records.add(mapper.apply(toRecord(row))));
        return records;
    }

    @SuppressWarnings( {"unchecked", "rawtypes"})
    protected VertxJooqRecord<?> toRecord(@NonNull Row row) {
        VertxJooqRecord<?> record = VertxJooqRecord.create((Table<VertxJooqRecord>) table());
        IntStream.range(0, row.size())
                 .mapToObj(row::getColumnName)
                 .map(this::lookupField)
                 .filter(Objects::nonNull)
                 .forEach(f -> record.set((Field<Object>) f, row.get(f.getType(), f.getName())));
        return record;
    }

}
