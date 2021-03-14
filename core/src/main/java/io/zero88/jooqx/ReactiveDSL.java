package io.zero88.jooqx;

import java.util.Collection;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.zero88.jooqx.ReactiveSQLImpl.ReactiveDSLAdapter;
import io.zero88.jooqx.ReactiveSQLImpl.ReactiveSQLRBC;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.adapter.SelectListAdapter;

import lombok.NonNull;

/**
 * DSL for for Vert.x Reactive SQL
 *
 * @see ReactiveSQLBatchCollector
 * @since 1.0.0
 */
public interface ReactiveDSL extends DSLAdapter<RowSet<Row>, ReactiveSQLResultCollector> {

    static @NonNull ReactiveDSL adapter() {
        return new ReactiveDSLAdapter();
    }

    /**
     * Batch
     *
     * @param table a query table context
     * @param <T>   Type of jOOQ Table in Query context
     * @return batch adapter
     */
    default <T extends TableLike<?>> SelectListAdapter<RowSet<Row>, ReactiveSQLBatchCollector, T, JsonRecord<?>,
                                                          JsonRecord<?>> batchJsonRecords(
        @NonNull T table) {
        return new SelectListAdapter<>(table, new ReactiveSQLRBC(), SQLResultAdapter.byJson(table));
    }

    /**
     * Batch
     *
     * @param table  a query table context
     * @param fields given fields
     * @param <T>    Type of jOOQ Table in Query context
     * @return batch adapter
     * @see TableLike
     */
    default <T extends TableLike<? extends Record>> SelectListAdapter<RowSet<Row>, ReactiveSQLBatchCollector, T,
                                                                         Record, Record> batch(
        @NonNull T table, @NonNull Collection<Field<?>> fields) {
        return new SelectListAdapter<>(table, new ReactiveSQLRBC(), SQLResultAdapter.byFields(fields));
    }

    /**
     * Batch
     *
     * @param table       a query table context
     * @param outputClass given output class
     * @param <T>         Type of jOOQ Table in Query context
     * @param <R>         Type ot output class
     * @return batch adapter
     * @see TableLike
     */
    default <T extends TableLike<? extends Record>, R> SelectListAdapter<RowSet<Row>, ReactiveSQLBatchCollector, T,
                                                                            JsonRecord<?>, R> batch(
        @NonNull T table, @NonNull Class<R> outputClass) {
        return new SelectListAdapter<>(table, new ReactiveSQLRBC(), SQLResultAdapter.byClass(table, outputClass));
    }

    /**
     * Batch
     *
     * @param table a query table context
     * @param <T>   Type of jOOQ Table in Query context
     * @return batch adapter
     * @see TableLike
     */
    default <T extends Table<R>, R extends Record> SelectListAdapter<RowSet<Row>, ReactiveSQLBatchCollector, T, R, R> batch(
        @NonNull T table) {
        return new SelectListAdapter<>(table, new ReactiveSQLRBC(), SQLResultAdapter.byTable(table));
    }

    /**
     * Batch
     *
     * @param <T>   Type of jOOQ Table in Query context
     * @param <R>   Type of record
     * @param <Z>   Type of expectation table
     * @param table a query table context
     * @return batch adapter
     * @see TableLike
     */
    default <T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> SelectListAdapter<RowSet<Row>, ReactiveSQLBatchCollector, T, R, R> batch(
        @NonNull T table, @NonNull Z toTable) {
        return new SelectListAdapter<>(table, new ReactiveSQLRBC(), SQLResultAdapter.byTable(toTable));
    }

    /**
     * Batch
     *
     * @param table  a query table context
     * @param record Type of record
     * @param <T>    Type of jOOQ Table in Query context
     * @param <R>    Type expectation record
     * @return batch adapter
     */
    default <T extends TableLike<? extends Record>, R extends Record> SelectListAdapter<RowSet<Row>,
                                                                                           ReactiveSQLBatchCollector,
                                                                                           T, R, R> batch(
        @NonNull T table, @NonNull R record) {
        return new SelectListAdapter<>(table, new ReactiveSQLRBC(), SQLResultAdapter.byRecord(record));
    }

}
