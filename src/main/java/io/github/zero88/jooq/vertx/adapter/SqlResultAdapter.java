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
 * @param <R>  Type of an expectation result
 */
public interface SqlResultAdapter<RS, T extends TableLike<? extends Record>, R> {

    /**
     * Declares Result set converter
     *
     * @return converter
     * @see ResultSetConverter
     */
    @NonNull ResultSetConverter<RS, T> converter();

    /**
     * Convert result set to expected result
     *
     * @param resultSet result set
     * @return result
     */
    @NonNull R convert(@NonNull RS resultSet);

}
