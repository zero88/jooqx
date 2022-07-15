package io.github.zero88.jooqx;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.TableRecord;
import org.jooq.impl.DSL;

import io.github.zero88.jooqx.adapter.RecordFactory;
import io.github.zero88.jooqx.adapter.SelectCount;
import io.github.zero88.jooqx.adapter.SelectExists;
import io.github.zero88.jooqx.adapter.SelectList;
import io.github.zero88.jooqx.adapter.SelectOne;

/**
 * DSL adapter
 *
 * @since 1.0.0
 */
public interface DSLAdapter {

    /**
     * Fetch count
     *
     * @return select count
     * @see SelectCount
     * @since 2.0.0
     */
    static SelectCount fetchCount() {
        return fetchCount(DSL.selectCount());
    }

    /**
     * Fetch count
     *
     * @param table a query table context
     * @return select count
     * @see SelectCount
     */
    static SelectCount fetchCount(@NotNull TableLike<Record1<Integer>> table) {
        return new SelectCount(table);
    }

    /**
     * Fetch exists
     *
     * @return select exists
     * @see SelectExists
     * @since 2.0.0
     */
    static SelectExists fetchExists() {
        return fetchExists(DSL.selectOne());
    }

    /**
     * Fetch exists
     *
     * @param table a query table context
     * @return select exists
     * @see SelectExists
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
    static <T extends TableLike<? extends Record>> SelectOne<JsonRecord<?>> fetchJsonRecord(@NotNull T table) {
        return new SelectOne<>(RecordFactory.byJson(table));
    }

    /**
     * Fetch one
     *
     * @param table  a query table context
     * @param record record
     * @param <T>    Type of jOOQ Table in Query context
     * @param <REC>  Type of output jOOQ record
     * @return select one adapter
     * @see TableLike
     */
    static <T extends TableLike<? extends Record>, REC extends TableRecord<REC>> SelectOne<REC> fetchOne(
        @NotNull T table, @NotNull REC record) {
        return new SelectOne<>(RecordFactory.byRecord(record));
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
    static <T extends TableLike<? extends Record>> SelectOne<Record> fetchOne(@NotNull T table,
                                                                              @NotNull Collection<Field<?>> fields) {
        return new SelectOne<>(RecordFactory.byFields(fields));
    }

    static SelectOne<Record> fetchOne(Field<?>... fields) {
        return new SelectOne<>(RecordFactory.byFields(fields));
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
    static <T extends TableLike<? extends Record>, R> SelectOne<R> fetchOne(@NotNull T table,
                                                                            @NotNull Class<R> outputClass) {
        return new SelectOne<>(RecordFactory.byClass(table, outputClass));
    }

    /**
     * Fetch one
     *
     * @param table a query table context
     * @param <T>   Type of jOOQ Table in Query context
     * @return select one adapter
     * @see TableLike
     */
    static <T extends Table<REC>, REC extends Record> SelectOne<REC> fetchOne(@NotNull T table) {
        return new SelectOne<>(RecordFactory.byTable(table));
    }

    /**
     * Fetch one
     *
     * @param <T>   Type of jOOQ Table in Query context
     * @param <REC> Type of record
     * @param <Z>   Type of expectation table
     * @param table a query table context
     * @return select one adapter
     * @see TableLike
     */
    static <T extends TableLike<? extends Record>, REC extends Record, Z extends Table<REC>> SelectOne<REC> fetchOne(
        @NotNull T table, @NotNull Z toTable) {
        return new SelectOne<>(RecordFactory.byConverter(table, r -> r.into(toTable)));
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
    static <T extends TableLike<? extends Record>> SelectList<JsonRecord<?>> fetchJsonRecords(@NotNull T table) {
        return new SelectList<>(RecordFactory.byJson(table));
    }

    /**
     * Fetch many
     *
     * @param table  a query table context
     * @param record record
     * @param <T>    Type of jOOQ Table in Query context
     * @param <REC>  Type of record
     * @return select many adapter
     * @see TableLike
     */
    static <T extends TableLike<? extends Record>, REC extends Record> SelectList<REC> fetchMany(@NotNull T table,
                                                                                                 @NotNull REC record) {
        return new SelectList<>(RecordFactory.byRecord(record));
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
    static <T extends TableLike<? extends Record>> SelectList<Record> fetchMany(@NotNull T table,
                                                                                @NotNull Collection<Field<?>> fields) {
        return new SelectList<>(RecordFactory.byFields(fields));
    }

    static SelectList<Record> fetchMany(Field<?>... fields) {
        return new SelectList<>(RecordFactory.byFields(fields));
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
    static <T extends TableLike<? extends Record>, R> SelectList<R> fetchMany(@NotNull T table,
                                                                              @NotNull Class<R> outputClass) {
        return new SelectList<>(RecordFactory.byClass(table, outputClass));
    }

    /**
     * Fetch many
     *
     * @param table a query table context
     * @param <T>   Type of jOOQ Table in Query context
     * @return select many adapter
     * @see TableLike
     */
    static <T extends Table<REC>, REC extends Record> SelectList<REC> fetchMany(@NotNull T table) {
        return new SelectList<>(RecordFactory.byTable(table));
    }

    /**
     * Fetch many
     *
     * @param <T>   Type of jOOQ Table in Query context
     * @param <REC> Type of record
     * @param <Z>   Type of expectation table
     * @param table a query table context
     * @return select many adapter
     * @see TableLike
     */
    static <T extends TableLike<? extends Record>, REC extends Record, Z extends Table<REC>> SelectList<REC> fetchMany(
        @NotNull T table, @NotNull Z toTable) {
        return new SelectList<>(RecordFactory.byConverter(table, r -> r.into(toTable)));
    }

}
