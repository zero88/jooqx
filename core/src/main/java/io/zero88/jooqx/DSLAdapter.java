package io.zero88.jooqx;

import java.util.Collection;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.TableRecord;

import io.zero88.jooqx.adapter.SelectCount;
import io.zero88.jooqx.adapter.SelectExists;
import io.zero88.jooqx.adapter.SelectList;
import io.zero88.jooqx.adapter.SelectOne;

import lombok.NonNull;

interface DSLAdapter<RS, C extends SQLResultCollector<RS>> {

    /**
     * Fetch count
     *
     * @param table a query table context
     * @return select count
     */
    SelectCount<RS, C> fetchCount(@NonNull TableLike<Record1<Integer>> table);

    /**
     * Fetch exists
     *
     * @param table a query table context
     * @return select exists
     */
    SelectExists<RS, C> fetchExists(@NonNull TableLike<Record1<Integer>> table);

    /**
     * Fetch one JsonRecord
     *
     * @param table a query table context
     * @param <T>   Type of jOOQ Table in Query context
     * @return select one adapter
     * @see TableLike
     * @see JsonRecord
     */
    <T extends TableLike<? extends Record>> SelectOne<RS, C, T, JsonRecord<?>, JsonRecord<?>> fetchJsonRecord(
        @NonNull T table);

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
    <T extends TableLike<? extends Record>, R extends TableRecord<R>> SelectOne<RS, C, T, R, R> fetchOne(
        @NonNull T table, @NonNull R record);

    /**
     * Fetch one
     *
     * @param table  a query table context
     * @param fields given fields
     * @param <T>    Type of jOOQ Table in Query context
     * @return select one adapter
     * @see TableLike
     */
    <T extends TableLike<? extends Record>> SelectOne<RS, C, T, Record, Record> fetchOne(@NonNull T table,
                                                                                         @NonNull Collection<Field<?>> fields);

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
    <T extends TableLike<? extends Record>, R> SelectOne<RS, C, T, JsonRecord<?>, R> fetchOne(@NonNull T table,
                                                                                              @NonNull Class<R> outputClass);

    /**
     * Fetch one
     *
     * @param table a query table context
     * @param <T>   Type of jOOQ Table in Query context
     * @return select one adapter
     * @see TableLike
     */
    <T extends Table<R>, R extends Record> SelectOne<RS, C, T, R, R> fetchOne(@NonNull T table);

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
    <T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> SelectOne<RS, C, T, R, R> fetchOne(
        @NonNull T table, @NonNull Z toTable);

    /**
     * Fetch many Json record
     *
     * @param table a query table context
     * @param <T>   Type of jOOQ Table in Query context
     * @return select many adapter
     * @see TableLike
     * @see JsonRecord
     */
    <T extends TableLike<? extends Record>> SelectList<RS, C, T, JsonRecord<?>, JsonRecord<?>> fetchJsonRecords(
        @NonNull T table);

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
    <T extends TableLike<? extends Record>, R extends TableRecord<R>> SelectList<RS, C, T, R, R> fetchMany(
        @NonNull T table, @NonNull R record);

    /**
     * Fetch many
     *
     * @param table  a query table context
     * @param fields given fields
     * @param <T>    Type of jOOQ Table in Query context
     * @return select many adapter
     * @see TableLike
     */
    <T extends TableLike<? extends Record>> SelectList<RS, C, T, Record, Record> fetchMany(@NonNull T table,
                                                                                           @NonNull Collection<Field<?>> fields);

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
    <T extends TableLike<? extends Record>, R> SelectList<RS, C, T, JsonRecord<?>, R> fetchMany(@NonNull T table,
                                                                                                @NonNull Class<R> outputClass);

    /**
     * Fetch many
     *
     * @param table a query table context
     * @param <T>   Type of jOOQ Table in Query context
     * @return select many adapter
     * @see TableLike
     */
    <T extends Table<R>, R extends Record> SelectList<RS, C, T, R, R> fetchMany(@NonNull T table);

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
    <T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> SelectList<RS, C, T, JsonRecord<?>,
                                                                                                R> fetchMany(
        @NonNull T table, @NonNull Z toTable);

}
