package io.github.zero88.jooq.vertx;

import java.util.Collection;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Table;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.adapter.SelectCountResultAdapter;
import io.github.zero88.jooq.vertx.adapter.SelectExistsResultAdapter;
import io.github.zero88.jooq.vertx.adapter.SelectListResultAdapter;
import io.github.zero88.jooq.vertx.adapter.SelectOneResultAdapter;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;

import lombok.NonNull;

/**
 * Vertx SQL DSL
 *
 * @param <RS> Type of SQL result set
 * @param <C>  Type of ResultSet Converter
 * @see ResultSetConverter
 * @since 1.0.0
 */
interface VertxSqlDSL<RS, C extends ResultSetConverter<RS>> {

    /**
     * Fetch count
     *
     * @param table given table
     * @return select count
     */
    SelectCountResultAdapter<RS, C> fetchCount(@NonNull TableLike<Record1<Integer>> table);

    /**
     * Fetch exists
     *
     * @param table given table
     * @return select exists
     */
    SelectExistsResultAdapter<RS, C> fetchExists(@NonNull TableLike<Record1<Integer>> table);

    /**
     * Fetch one
     *
     * @param table given table
     * @param <T>   Type of table
     * @return select one
     * @see TableLike
     */
    <T extends TableLike<? extends Record>> SelectOneResultAdapter<RS, C, T, VertxJooqRecord<?>> fetchVertxRecord(
        @NonNull T table);

    /**
     * Fetch one
     *
     * @param table  given table
     * @param record record
     * @param <T>    Type of table
     * @param <R>    Type of record
     * @return select one
     * @see TableLike
     */
    <T extends TableLike<? extends Record>, R extends Record> SelectOneResultAdapter<RS, C, T, R> fetchOne(
        @NonNull T table, @NonNull R record);

    /**
     * Fetch one
     *
     * @param table  given table
     * @param fields given fields
     * @param <T>    Type of table
     * @return select one
     * @see TableLike
     */
    <T extends TableLike<? extends Record>> SelectOneResultAdapter<RS, C, T, Record> fetchOne(@NonNull T table,
                                                                                              @NonNull Collection<Field<?>> fields);

    /**
     * Fetch one
     *
     * @param table       given table
     * @param outputClass given output class
     * @param <T>         Type of table
     * @param <R>         Type ot output class
     * @return select one
     * @see TableLike
     */
    <T extends TableLike<? extends Record>, R> SelectOneResultAdapter<RS, C, T, R> fetchOne(@NonNull T table,
                                                                                            @NonNull Class<R> outputClass);

    /**
     * Fetch one
     *
     * @param table given table
     * @param <T>   Type of table
     * @return select one
     * @see TableLike
     */
    <T extends Table<? extends Record>, R extends Record> SelectOneResultAdapter<RS, C, T, R> fetchOne(
        @NonNull T table);

    /**
     * Fetch one
     *
     * @param <T>   Type of table
     * @param <R>   Type of record
     * @param <Z>   Type of expectation table
     * @param table given table
     * @return select one
     * @see TableLike
     */
    <T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> SelectOneResultAdapter<RS, C, T, R> fetchOne(
        @NonNull T table, @NonNull Z toTable);

    /**
     * Fetch many Vertx record
     *
     * @param table given table
     * @param <T>   Type of table
     * @return select many
     * @see TableLike
     */
    <T extends TableLike<? extends Record>> SelectListResultAdapter<RS, C, T, VertxJooqRecord<?>> fetchVertxRecords(
        @NonNull T table);

    /**
     * Fetch many
     *
     * @param table  given table
     * @param record record
     * @param <T>    Type of table
     * @param <R>    Type of record
     * @return select many
     * @see TableLike
     */
    <T extends TableLike<? extends Record>, R extends Record> SelectListResultAdapter<RS, C, T, R> fetchMany(
        @NonNull T table, @NonNull R record);

    /**
     * Fetch many
     *
     * @param table  given table
     * @param fields given fields
     * @param <T>    Type of table
     * @return select many
     * @see TableLike
     */
    <T extends TableLike<? extends Record>> SelectListResultAdapter<RS, C, T, Record> fetchMany(@NonNull T table,
                                                                                                @NonNull Collection<Field<?>> fields);

    /**
     * Fetch many
     *
     * @param table       given table
     * @param outputClass given output class
     * @param <T>         Type of table
     * @param <R>         Type ot output class
     * @return select many
     * @see TableLike
     */
    <T extends TableLike<? extends Record>, R> SelectListResultAdapter<RS, C, T, R> fetchMany(@NonNull T table,
                                                                                              @NonNull Class<R> outputClass);

    /**
     * Fetch many
     *
     * @param table given table
     * @param <T>   Type of table
     * @return select many
     * @see TableLike
     */
    <T extends Table<? extends Record>, R extends Record> SelectListResultAdapter<RS, C, T, R> fetchMany(
        @NonNull T table);

    /**
     * Fetch many
     *
     * @param <T>   Type of table
     * @param <R>   Type of record
     * @param <Z>   Type of expectation table
     * @param table given table
     * @return select many
     * @see TableLike
     */
    <T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> SelectListResultAdapter<RS, C, T,
                                                                                                             R> fetchMany(
        @NonNull T table, @NonNull Z toTable);

}
