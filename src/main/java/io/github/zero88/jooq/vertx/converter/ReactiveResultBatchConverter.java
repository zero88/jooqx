package io.github.zero88.jooq.vertx.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jooq.Record;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.record.VertxJooqRecord;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import lombok.NonNull;

/**
 * @param <T> Type of jOOQ Table
 * @see Record
 * @see TableLike
 */
public class ReactiveResultBatchConverter<T extends TableLike<? extends Record>> extends ReactiveResultSetConverter<T>
    implements ResultSetBatchConverter<RowSet<Row>, T> {

    public ReactiveResultBatchConverter(@NonNull T table) {
        super(table);
    }

    @Override
    protected <R> List<R> doConvert(@NonNull RowSet<Row> resultSet, @NonNull Function<VertxJooqRecord<?>, R> mapper) {
        final List<R> records = new ArrayList<>();
        while (resultSet != null) {
            final List<R> rs = super.doConvert(resultSet, mapper);
            if (!rs.isEmpty()) {
                records.add(rs.get(0));
            }
            resultSet = resultSet.next();
        }
        return records;
    }

}
