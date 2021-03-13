package io.zero88.jooqx;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zero88.jooqx.adapter.RowConverterStrategy;

import lombok.NonNull;

/**
 * Represents for a converter that transforms SQL result set to jOOQ record
 *
 * @param <RS> Type of Vertx SQL result set
 * @see LegacySQLConverter
 * @see ReactiveSQLResultConverter
 * @see ReactiveSQLBatchConverter
 * @since 1.0.0
 */
public interface SQLResultConverter<RS> {

    Logger LOGGER = LoggerFactory.getLogger(SQLResultConverter.class);

    /**
     * Collect result set to an expectation result
     *
     * @param resultSet result set
     * @param strategy  row converter strategy
     * @param <R>       Type of output
     * @return list output
     */
    @NonNull <R> List<R> collect(@NonNull RS resultSet, @NonNull RowConverterStrategy<R> strategy);

}
