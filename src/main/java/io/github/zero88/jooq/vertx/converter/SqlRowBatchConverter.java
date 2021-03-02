package io.github.zero88.jooq.vertx.converter;

import java.util.ArrayList;
import java.util.List;

import org.jooq.Record;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.record.VertxJooqRecord;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import lombok.NonNull;

/**
 *
 * @param <T> Type of jOOQ Table
 * @see Record
 * @see TableLike
 */
public class SqlRowBatchConverter<T extends TableLike<? extends Record>> extends SqlRowRecordConverter<T> {

    public SqlRowBatchConverter(@NonNull T table) {
        super(table);
    }

    public List<VertxJooqRecord<?>> convert(@NonNull RowSet<Row> resultSet) {
        final List<VertxJooqRecord<?>> records = new ArrayList<>();
        while (resultSet != null) {
            final List<VertxJooqRecord<?>> rs = super.convert(resultSet);
            if (!rs.isEmpty()) {
                records.add(rs.get(0));
            }
            resultSet = resultSet.next();
        }
        return records;
    }

}
