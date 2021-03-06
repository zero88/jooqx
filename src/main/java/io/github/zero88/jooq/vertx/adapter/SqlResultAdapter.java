package io.github.zero88.jooq.vertx.adapter;

import org.jooq.Record;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.converter.ResultSetConverter;

import lombok.NonNull;

/**
 * SQL Result adapter receives Result set then mapping to expected result
 *
 * @param <RS> Type of Vertx Result set
 * @param <T>  Type of jOOQ Table
 * @param <C>  Type of result set converter
 * @param <R>  Type of an expectation result
 * @see TableLike
 * @see ResultSetConverter
 * @since 1.0.0
 */
public interface SqlResultAdapter<RS, C extends ResultSetConverter<RS>, T extends TableLike<? extends Record>, R> {

    /**
     * A current context holder
     *
     * @return jOOQ table
     * @see TableLike
     */
    T table();

    /**
     * Declares Result set converter
     *
     * @return converter
     */
    @NonNull C converter();

    /**
     * Convert result set to expected result
     *
     * @param resultSet result set
     * @return result
     */
    @NonNull R convert(@NonNull RS resultSet);

}
