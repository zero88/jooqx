package io.github.zero88.jooq.vertx.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.jooq.Record;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.VertxJooqRecord;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import lombok.NonNull;

/**
 * @see Record
 * @see TableLike
 */
public final class ReactiveResultBatchConverter extends ReactiveResultSetConverter
    implements ResultBatchConverter<RowSet<Row>, RowSet<Row>> {

    @Override
    protected <T extends TableLike<? extends Record>, R> List<R> doConvert(@NonNull RowSet<Row> resultSet,
                                                                           @NonNull T table,
                                                                           @NonNull Function<VertxJooqRecord<?>, R> mapper) {
        final List<R> records = new ArrayList<>();
        while (resultSet != null) {
            final List<R> rs = super.doConvert(resultSet, table, mapper);
            if (!rs.isEmpty()) {
                records.add(rs.get(0));
            }
            resultSet = resultSet.next();
        }
        return records;
    }

    @Override
    public int batchResultSize(@NonNull RowSet<Row> batchResult) {
        final int[] count = new int[] {0};
        while (batchResult != null) {
            count[0]++;
            batchResult = batchResult.next();
        }
        return count[0];
    }

}
