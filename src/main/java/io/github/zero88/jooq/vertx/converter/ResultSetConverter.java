package io.github.zero88.jooq.vertx.converter;

import java.util.Collection;
import java.util.List;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.jooq.vertx.VertxJooqRecord;
import io.github.zero88.jooq.vertx.adapter.SelectStrategy;

import lombok.NonNull;

/**
 * Represents for a converter that transforms SQL result set to jOOQ record
 *
 * @param <RS> Type of Vertx SQL result set
 * @since 1.0.0
 */
public interface ResultSetConverter<RS> {

    Logger LOGGER = LoggerFactory.getLogger(ResultSetConverter.class);

    /**
     * Setup strategy to handle result set
     *
     * @param strategy select strategy
     * @return a reference to this for fluent API
     */
    @NonNull ResultSetConverter<RS> setup(@NonNull SelectStrategy strategy);

    /**
     * Convert SQL result set to generic {@code Vertx jOOQ record}
     *
     * @param resultSet SQL result set
     * @param table     table context
     * @param <T>       Type of Jooq Table Like
     * @return list of {@code Vertx jOOQ record}
     * @see VertxJooqRecord
     * @see TableLike
     */
    <T extends TableLike<? extends Record>> List<VertxJooqRecord<?>> convertVertxRecord(@NonNull RS resultSet,
                                                                                        @NonNull T table);

    /**
     * Convert result set to an expectation record
     *
     * @param <R>       Type of record
     * @param <T>       Type of Jooq Table Like
     * @param resultSet SQL result set
     * @param table     table context
     * @return record list
     * @see Record#into(Table)
     * @see Table
     */
    <T extends Table<? extends Record>, R extends Record> List<R> convert(@NonNull RS resultSet, @NonNull T table);

    /**
     * Convert result set to an expectation record
     *
     * @param <R>       Type of record
     * @param <T>       Type of Jooq Table Like
     * @param resultSet SQL result set
     * @param table     table context
     * @param toTable   table instance
     * @return record list
     * @see Record#into(Table)
     * @see TableLike
     */
    <T extends TableLike<? extends Record>, R extends Record> List<R> convert(@NonNull RS resultSet, @NonNull T table,
                                                                              @NonNull Table<R> toTable);

    /**
     * Convert result set to an expectation record
     *
     * @param <R>       Type of record
     * @param <T>       Type of Jooq Table Like
     * @param resultSet SQL result set
     * @param table     table context
     * @param record    dummy record
     * @return record list
     * @see Record#into(Table)
     * @see TableLike
     */
    <T extends TableLike<? extends Record>, R extends Record> List<R> convert(@NonNull RS resultSet, @NonNull T table,
                                                                              @NonNull R record);

    /**
     * Convert result set to an expectation record
     *
     * @param <T>       Type of Jooq Table Like
     * @param resultSet SQL result set
     * @param table     table context
     * @param fields    dummy record
     * @return record list
     * @see Record#into(Table)
     * @see TableLike
     */
    <T extends TableLike<? extends Record>> List<Record> convert(@NonNull RS resultSet, @NonNull T table,
                                                                 @NonNull Collection<Field<?>> fields);

    /**
     * Convert result set to an expectation record
     *
     * @param <T>         Type of Jooq Table Like
     * @param <R>         Type of an expectation record
     * @param resultSet   SQL result set
     * @param table       table context
     * @param recordClass record class. Might be pojo or custom object as well
     * @return record list
     * @see Record#into(Object)
     * @see TableLike
     */
    <T extends TableLike<? extends Record>, R> List<R> convert(@NonNull RS resultSet, @NonNull T table,
                                                               @NonNull Class<R> recordClass);

}
