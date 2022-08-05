package io.github.zero88.jooqx;

import java.util.Collection;

import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Table;
import org.jooq.TableLike;
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
     * @since 1.0.0
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
     * @since 1.0.0
     */
    static SelectExists fetchExists(@NotNull TableLike<Record1<Integer>> table) {
        return new SelectExists(table);
    }

    /**
     * Fetch one JsonRecord
     *
     * @param table a query table context
     * @return select one adapter
     * @see TableLike
     * @see JsonRecord
     * @since 1.0.0
     */
    static SelectOne<JsonRecord<?>> fetchJsonRecord(@NotNull TableLike<? extends Record> table) {
        return new SelectOne<>(RecordFactory.byJson(table));
    }

    /**
     * Fetch one
     *
     * @param table a query table context
     * @param <T>   Type of jOOQ Table in Query context
     * @return select one adapter
     * @see TableLike
     * @since 1.0.0
     */
    static <T extends Table<REC>, REC extends Record> SelectOne<REC> fetchOne(@NotNull T table) {
        return new SelectOne<>(RecordFactory.byTable(table));
    }

    /**
     * Fetch one record by the record definition
     *
     * @param <REC>  Type of output jOOQ record
     * @param record record
     * @return select one adapter
     * @see Record
     * @since 2.0.0
     */
    static <REC extends Record> SelectOne<REC> fetchOne(@NotNull REC record) {
        return new SelectOne<>(RecordFactory.byRecord(record));
    }

    /**
     * Fetch one record by a table of the query context then map to another record type
     *
     * @param table  a query table context
     * @param record record
     * @param <REC>  Type of output jOOQ record
     * @return select one adapter
     * @see TableLike
     * @see Record
     * @since 1.0.0
     */
    static <REC extends Record> SelectOne<REC> fetchOne(@NotNull TableLike<? extends Record> table,
                                                        @NotNull REC record) {
        return new SelectOne<>(RecordFactory.byMapper(table, rec -> rec.into(DSL.table(record))));
    }

    /**
     * Fetch one record by a table of the query context then map into target fields
     *
     * @param table  a query table context
     * @param fields a given target fields
     * @return select one adapter
     * @see TableLike
     * @see Field
     * @since 1.0.0
     */
    static SelectOne<Record> fetchOne(@NotNull TableLike<? extends Record> table,
                                      @NotNull Collection<Field<?>> fields) {
        return new SelectOne<>(RecordFactory.byMapper(table, rec -> rec.into(fields.stream().toArray(Field[]::new))));
    }

    /**
     * Fetch one record by fields
     *
     * @param fields given fields
     * @return select one adapter
     * @see Field
     * @since 2.0.0
     */
    static SelectOne<Record> fetchOne(Field<?>... fields) {
        return new SelectOne<>(RecordFactory.byFields(fields));
    }

    /**
     * Fetch one record by a table of the query context then map to custom type by given output class
     *
     * @param <R>         Type ot output class
     * @param table       a query table context
     * @param outputClass given output class
     * @return select one adapter
     * @see TableLike
     * @see Record
     * @since 1.0.0
     */
    static <R> SelectOne<R> fetchOne(@NotNull TableLike<? extends Record> table, @NotNull Class<R> outputClass) {
        return new SelectOne<>(RecordFactory.byClass(table, outputClass));
    }

    /**
     * Fetch one record by a table of the query context then map into target record that defines in target table
     *
     * @param <R>     Type of expectation record
     * @param <T>     Type of expectation table
     * @param table   a query table context
     * @param toTable a target table
     * @return select one adapter
     * @see TableLike
     * @since 1.0.0
     */
    static <R extends Record, T extends Table<R>> SelectOne<R> fetchOne(@NotNull TableLike<? extends Record> table,
                                                                        @NotNull T toTable) {
        return new SelectOne<>(RecordFactory.byMapper(table, r -> r.into(toTable)));
    }

    /**
     * Fetch many Json record
     *
     * @param table a query table context
     * @return select many adapter
     * @see TableLike
     * @see JsonRecord
     * @since 1.0.0
     */
    static SelectList<JsonRecord<?>> fetchJsonRecords(@NotNull TableLike<? extends Record> table) {
        return new SelectList<>(RecordFactory.byJson(table));
    }

    /**
     * Fetch many
     *
     * @param table a query table context
     * @param <T>   Type of jOOQ Table in Query context
     * @return select many adapter
     * @see TableLike
     * @see Record
     * @since 1.0.0
     */
    static <T extends Table<REC>, REC extends Record> SelectList<REC> fetchMany(@NotNull T table) {
        return new SelectList<>(RecordFactory.byTable(table));
    }

    /**
     * Fetch many records by the record definition
     *
     * @param <REC>  Type of record
     * @param record record
     * @return select many adapter
     * @see Record
     * @since 2.0.0
     */
    static <REC extends Record> SelectList<REC> fetchMany(@NotNull REC record) {
        return new SelectList<>(RecordFactory.byRecord(record));
    }

    /**
     * Fetch many by a table of the query context then map to another record type
     *
     * @param table  a query table context
     * @param record record
     * @param <REC>  Type of record
     * @return select many adapter
     * @see TableLike
     * @see Record
     * @since 1.0.0
     */
    static <REC extends Record> SelectList<REC> fetchMany(@NotNull TableLike<? extends Record> table,
                                                          @NotNull REC record) {
        return new SelectList<>(RecordFactory.byMapper(table, rec -> rec.into(DSL.table(record))));
    }

    /**
     * Fetch many by a table of the query context then map into target fields
     *
     * @param table  a query table context
     * @param fields given fields
     * @return select many adapter
     * @see TableLike
     * @since 1.0.0
     */
    static SelectList<Record> fetchMany(@NotNull TableLike<? extends Record> table,
                                        @NotNull Collection<Field<?>> fields) {
        return new SelectList<>(RecordFactory.byMapper(table, rec -> rec.into(fields.stream().toArray(Field[]::new))));
    }

    /**
     * Fetch many records by fields
     *
     * @param fields the given fields
     * @return select many adapter
     * @see Field
     * @since 2.0.0
     */
    static SelectList<Record> fetchMany(Field<?>... fields) {
        return new SelectList<>(RecordFactory.byFields(fields));
    }

    /**
     * Fetch many records by a table of the query context then map to custom type by given output class
     *
     * @param <R>         Type ot output class
     * @param table       a query table context
     * @param outputClass a given output class
     * @return select many adapter
     * @see TableLike
     * @since 1.0.0
     */
    static <R> SelectList<R> fetchMany(@NotNull TableLike<? extends Record> table, @NotNull Class<R> outputClass) {
        return new SelectList<>(RecordFactory.byClass(table, outputClass));
    }

    /**
     * Fetch many by a table of the query context then map into target record that defines in target table
     *
     * @param <R>     Type of record
     * @param <T>     Type of expectation table
     * @param table   a query table context
     * @param toTable a target table
     * @return select many adapter
     * @see TableLike
     * @see Record
     * @since 1.0.0
     */
    static <R extends Record, T extends Table<R>> SelectList<R> fetchMany(@NotNull TableLike<? extends Record> table,
                                                                          @NotNull T toTable) {
        return new SelectList<>(RecordFactory.byMapper(table, r -> r.into(toTable)));
    }

}
