package io.zero88.jooqx;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.codegen.annotations.GenIgnore;
import io.zero88.jooqx.adapter.RowConverterStrategy;
import io.zero88.jooqx.adapter.SelectStrategy;

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

    @GenIgnore
    Logger LOGGER = LoggerFactory.getLogger(SQLResultCollector.class);

    @GenIgnore
    default void warnManyResult(boolean check, @NonNull SelectStrategy strategy) {
        if (check) {
            LOGGER.warn("Query strategy is [{}] but query result contains more than one row", strategy);
        }
    }

    /**
     * Collect result set to an expectation result
     *
     * @param <T>       the type of input elements to the reduction operation
     * @param <R>       the result type of the reduction operation
     * @param resultSet result set
     * @param strategy  row converter strategy
     * @return list output
     */
    @NonNull <T, R> List<R> collect(@NonNull RS resultSet, @NonNull RowConverterStrategy<T, R> strategy);

}
