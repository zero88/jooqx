package io.github.zero88.jooq.vertx.converter;

import java.util.ArrayList;
import java.util.List;

import org.jooq.Record;
import org.jooq.Table;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import lombok.NonNull;

/**
 * Bug <a href="vertx-sql-client#909">https://github.com/eclipse-vertx/vertx-sql-client/issues/909</a>
 *
 * @param <R> Type of Jooq Record
 * @param <T> Type of Jooq Table
 * @see Record
 * @see Table
 */
public class BatchRowSetConverter<R extends Record, T extends Table<R>> extends RowSetConverter<R, T> {

    public BatchRowSetConverter(@NonNull T table, @NonNull Class<R> recordClass) {
        super(table, recordClass);
    }

    public List<R> convert(@NonNull RowSet<Row> resultSet) {
        final List<R> records = new ArrayList<>();
        while (resultSet != null) {
            final List<R> rs = super.convert(resultSet);
            if (!rs.isEmpty()) {
                records.add(rs.get(0));
            }
            resultSet = resultSet.next();
        }
        return records;
    }

}
