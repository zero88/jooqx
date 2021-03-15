package io.zero88.jooqx;

import java.util.List;

import org.jooq.Record;
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
     * @param <R>       Type of output
     * @param resultSet result set
     * @param strategy  row converter strategy
     * @return list output
     */
    @NonNull <R extends Record, O> List<O> collect(@NonNull RS resultSet, @NonNull RowConverterStrategy<R, O> strategy);

}
