package io.github.zero88.jooq.vertx.converter;

import java.util.List;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.record.VertxJooqRecord;

import lombok.NonNull;

/**
 * Represents for a converter that transforms SQL result set to jOOQ record
 *
 * @param <RS> Type of Vertx SQL result set
 * @param <T>  Type of Jooq Table Like
 * @see Record
 * @see TableLike
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public interface ResultSetConverter<RS, T extends TableLike<? extends Record>> {

    /**
     * A current context holder
     *
     * @return jOOQ table
     * @see TableLike
     */
    T table();

    /**
     * Lookup field by name
     *
     * @param field field name
     * @return jOOQ Field
     * @see Field
     */
    Field lookupField(String field);

    /**
     * Convert SQL result set to generic {@code Vertx jOOQ record}
     *
     * @param resultSet SQL result set
     * @return list of {@code Vertx jOOQ record}
     * @see VertxJooqRecord
     */
    List<VertxJooqRecord<?>> convert(@NonNull RS resultSet);

    /**
     * Convert result set to an expectation record
     *
     * @param <R>       Type of record
     * @param resultSet SQL result set
     * @param table     table instance
     * @return record list
     * @see Record#into(Table)
     */
    <R extends Record> List<R> convert(@NonNull RS resultSet, @NonNull Table<R> table);

    /**
     * Convert result set to an expectation record
     *
     * @param <R>       Type of an expectation record
     * @param resultSet SQL result set
     * @param record    record class
     * @return record list
     * @see Record#into(Object)
     */
    <R> List<R> convert(@NonNull RS resultSet, @NonNull Class<R> record);

}
