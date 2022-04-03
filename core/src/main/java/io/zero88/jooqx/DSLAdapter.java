package io.zero88.jooqx;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.TableRecord;

import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.adapter.SelectCount;
import io.zero88.jooqx.adapter.SelectExists;
import io.zero88.jooqx.adapter.SelectList;
import io.zero88.jooqx.adapter.SelectOne;

/**
 * DSL adapter
 *
 * @since 1.0.0
 */
public interface DSLAdapter {

    /**
     * Fetch count
     *
     * @param table a query table context
     * @return select count
     */
    static SelectCount fetchCount(@NotNull TableLike<Record1<Integer>> table) {
        return new SelectCount(table);
    }

    /**
     * Fetch exists
     *
     * @param table a query table context
     * @return select exists
     */
    static SelectExists fetchExists(@NotNull TableLike<Record1<Integer>> table) {
        return new SelectExists(table);
    }

    /**
     * Fetch one JsonRecord
     *
     * @param table a query table context
     * @param <T>   Type of jOOQ Table in Query context
     * @return select one adapter
     * @see TableLike
     * @see JsonRecord
     */
    static <T extends TableLike<? extends Record>> SelectOne<T, JsonRecord<?>, JsonRecord<?>> fetchJsonRecord(
        @NotNull T table) {
        return new SelectOne<>(table, SQLResultAdapter.byJson());
    }

    /**
     * Fetch one
     *
     * @param table  a query table context
     * @param record record
     * @param <T>    Type of jOOQ Table in Query context
     * @param <R>    Type of output jOOQ record
     * @return select one adapter
     * @see TableLike
     */
    static <T extends TableLike<? extends Record>, R extends TableRecord<R>> SelectOne<T, R, R> fetchOne(
        @NotNull T table, @NotNull R record) {
        return new SelectOne<>(table, SQLResultAdapter.byRecord(record));
    }

    /**
     * Fetch one
     *
     * @param table  a query table context
     * @param fields given fields
     * @param <T>    Type of jOOQ Table in Query context
     * @return select one adapter
     * @see TableLike
     */
    static <T extends TableLike<? extends Record>> SelectOne<T, Record, Record> fetchOne(@NotNull T table,
                                                                                         @NotNull Collection<Field<?>> fields) {
        return new SelectOne<>(table, SQLResultAdapter.byFields(fields));
    }

    /**
     * Fetch one
     *
     * @param table       a query table context
     * @param outputClass given output class
     * @param <T>         Type of jOOQ Table in Query context
     * @param <R>         Type ot output class
     * @return select one adapter
     * @see TableLike
     */
    static <T extends TableLike<? extends Record>, R> SelectOne<T, JsonRecord<?>, R> fetchOne(@NotNull T table,
                                                                                              @NotNull Class<R> outputClass) {
        return new SelectOne<>(table, SQLResultAdapter.byClass(outputClass));
    }

    /**
     * Fetch one
     *
     * @param table a query table context
     * @param <T>   Type of jOOQ Table in Query context
     * @return select one adapter
     * @see TableLike
     */
    static <T extends Table<R>, R extends Record> SelectOne<T, R, R> fetchOne(@NotNull T table) {
        return new SelectOne<>(table, SQLResultAdapter.byTable(table));
    }

    /**
     * Fetch one
     *
     * @param <T>   Type of jOOQ Table in Query context
     * @param <R>   Type of record
     * @param <Z>   Type of expectation table
     * @param table a query table context
     * @return select one adapter
     * @see TableLike
     */
    static <T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> SelectOne<T, R, R> fetchOne(
        @NotNull T table, @NotNull Z toTable) {
        return new SelectOne<>(table, SQLResultAdapter.byTable(toTable));
    }

    /**
     * Fetch many Json record
     *
     * @param table a query table context
     * @param <T>   Type of jOOQ Table in Query context
     * @return select many adapter
     * @see TableLike
     * @see JsonRecord
     */
    static <T extends TableLike<? extends Record>> SelectList<T, JsonRecord<?>, JsonRecord<?>> fetchJsonRecords(
        @NotNull T table) {
        return new SelectList<>(table, SQLResultAdapter.byJson());
    }

    /**
     * Fetch many
     *
     * @param table  a query table context
     * @param record record
     * @param <T>    Type of jOOQ Table in Query context
     * @param <R>    Type of record
     * @return select many adapter
     * @see TableLike
     */
    static <T extends TableLike<? extends Record>, R extends Record> SelectList<T, R, R> fetchMany(@NotNull T table,
                                                                                                   @NotNull R record) {
        return new SelectList<>(table, SQLResultAdapter.byRecord(record));
    }

    /**
     * Fetch many
     *
     * @param table  a query table context
     * @param fields given fields
     * @param <T>    Type of jOOQ Table in Query context
     * @return select many adapter
     * @see TableLike
     */
    static <T extends TableLike<? extends Record>> SelectList<T, Record, Record> fetchMany(@NotNull T table,
                                                                                           @NotNull Collection<Field<?>> fields) {
        return new SelectList<>(table, SQLResultAdapter.byFields(fields));
    }

    /**
     * Fetch many
     *
     * @param table       a query table context
     * @param outputClass given output class
     * @param <T>         Type of jOOQ Table in Query context
     * @param <R>         Type ot output class
     * @return select many adapter
     * @see TableLike
     */
    static <T extends TableLike<? extends Record>, R> SelectList<T, JsonRecord<?>, R> fetchMany(@NotNull T table,
                                                                                                @NotNull Class<R> outputClass) {
        return new SelectList<>(table, SQLResultAdapter.byClass(outputClass));
    }

    /**
     * Fetch many
     *
     * @param table a query table context
     * @param <T>   Type of jOOQ Table in Query context
     * @return select many adapter
     * @see TableLike
     */
    static <T extends Table<R>, R extends Record> SelectList<T, R, R> fetchMany(@NotNull T table) {
        return new SelectList<>(table, SQLResultAdapter.byTable(table));
    }

    /**
     * Fetch many
     *
     * @param <T>   Type of jOOQ Table in Query context
     * @param <R>   Type of record
     * @param <Z>   Type of expectation table
     * @param table a query table context
     * @return select many adapter
     * @see TableLike
     */
    static <T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> SelectList<T, JsonRecord<?>,
                                                                                                       R> fetchMany(
        @NotNull T table, @NotNull Z toTable) {
        return new SelectList<>(table, SQLResultAdapter.byJson().andThen(r -> r.into(toTable)));
    }

}
