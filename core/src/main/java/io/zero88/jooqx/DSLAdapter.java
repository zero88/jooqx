package io.zero88.jooqx;

import java.util.Collection;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Table;
import org.jooq.TableLike;

import io.zero88.jooqx.adapter.SelectCountAdapter;
import io.zero88.jooqx.adapter.SelectExistsAdapter;
import io.zero88.jooqx.adapter.SelectListAdapter;
import io.zero88.jooqx.adapter.SelectOneAdapter;

import lombok.NonNull;

interface DSLAdapter<RS, C extends SQLResultConverter<RS>> {

    /**
     * Fetch count
     *
     * @param table given table
     * @return select count
     */
    SelectCountAdapter<RS, C> fetchCount(@NonNull TableLike<Record1<Integer>> table);

    /**
     * Fetch exists
     *
     * @param table given table
     * @return select exists
     */
    SelectExistsAdapter<RS, C> fetchExists(@NonNull TableLike<Record1<Integer>> table);

    /**
     * Fetch one JsonRecord
     *
     * @param table given table
     * @param <T>   Type of table
     * @return select one adapter
     * @see TableLike
     * @see JsonRecord
     */
    <T extends TableLike<? extends Record>> SelectOneAdapter<RS, C, T, JsonRecord<?>> fetchJsonRecord(
        @NonNull T table);

    /**
     * Fetch one
     *
     * @param table  given table
     * @param record record
     * @param <T>    Type of table
     * @param <R>    Type of record
     * @return select one adapter
     * @see TableLike
     */
    <T extends TableLike<? extends Record>, R extends Record> SelectOneAdapter<RS, C, T, R> fetchOne(
        @NonNull T table, @NonNull R record);

    /**
     * Fetch one
     *
     * @param table  given table
     * @param fields given fields
     * @param <T>    Type of table
     * @return select one adapter
     * @see TableLike
     */
    <T extends TableLike<? extends Record>> SelectOneAdapter<RS, C, T, Record> fetchOne(@NonNull T table,
                                                                                        @NonNull Collection<Field<?>> fields);

    /**
     * Fetch one
     *
     * @param table       given table
     * @param outputClass given output class
     * @param <T>         Type of table
     * @param <R>         Type ot output class
     * @return select one adapter
     * @see TableLike
     */
    <T extends TableLike<? extends Record>, R> SelectOneAdapter<RS, C, T, R> fetchOne(@NonNull T table,
                                                                                      @NonNull Class<R> outputClass);

    /**
     * Fetch one
     *
     * @param table given table
     * @param <T>   Type of table
     * @return select one adapter
     * @see TableLike
     */
    <T extends Table<R>, R extends Record> SelectOneAdapter<RS, C, T, R> fetchOne(@NonNull T table);

    /**
     * Fetch one
     *
     * @param <T>   Type of table
     * @param <R>   Type of record
     * @param <Z>   Type of expectation table
     * @param table given table
     * @return select one adapter
     * @see TableLike
     */
    <T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> SelectOneAdapter<RS, C, T, R> fetchOne(
        @NonNull T table, @NonNull Z toTable);

    /**
     * Fetch many Json record
     *
     * @param table given table
     * @param <T>   Type of table
     * @return select many adapter
     * @see TableLike
     * @see JsonRecord
     */
    <T extends TableLike<? extends Record>> SelectListAdapter<RS, C, T, JsonRecord<?>> fetchJsonRecords(
        @NonNull T table);

    /**
     * Fetch many
     *
     * @param table  given table
     * @param record record
     * @param <T>    Type of table
     * @param <R>    Type of record
     * @return select many adapter
     * @see TableLike
     */
    <T extends TableLike<? extends Record>, R extends Record> SelectListAdapter<RS, C, T, R> fetchMany(
        @NonNull T table, @NonNull R record);

    /**
     * Fetch many
     *
     * @param table  given table
     * @param fields given fields
     * @param <T>    Type of table
     * @return select many adapter
     * @see TableLike
     */
    <T extends TableLike<? extends Record>> SelectListAdapter<RS, C, T, Record> fetchMany(@NonNull T table,
                                                                                          @NonNull Collection<Field<?>> fields);

    /**
     * Fetch many
     *
     * @param table       given table
     * @param outputClass given output class
     * @param <T>         Type of table
     * @param <R>         Type ot output class
     * @return select many adapter
     * @see TableLike
     */
    <T extends TableLike<? extends Record>, R> SelectListAdapter<RS, C, T, R> fetchMany(@NonNull T table,
                                                                                        @NonNull Class<R> outputClass);

    /**
     * Fetch many
     *
     * @param table given table
     * @param <T>   Type of table
     * @return select many adapter
     * @see TableLike
     */
    <T extends Table<R>, R extends Record> SelectListAdapter<RS, C, T, R> fetchMany(@NonNull T table);

    /**
     * Fetch many
     *
     * @param <T>   Type of table
     * @param <R>   Type of record
     * @param <Z>   Type of expectation table
     * @param table given table
     * @return select many adapter
     * @see TableLike
     */
    <T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> SelectListAdapter<RS, C, T,
                                                                                                                 R> fetchMany(
        @NonNull T table, @NonNull Z toTable);

}
