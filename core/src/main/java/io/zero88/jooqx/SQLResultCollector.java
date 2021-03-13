package io.zero88.jooqx;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.zero88.jooqx.adapter.RowConverterStrategy;

import lombok.NonNull;

/**
 * Represents for a collector that collects {@code Vert.x SQL result} to an expectation output
 *
 * @param <RS> Type of Vertx SQL result set
 * @see LegacySQLCollector
 * @see ReactiveSQLResultCollector
 * @see ReactiveSQLBatchCollector
 * @since 1.0.0
 */
public interface SQLResultCollector<RS> {

    Logger LOGGER = LoggerFactory.getLogger(SQLResultCollector.class);

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
