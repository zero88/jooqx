package io.github.zero88.jooqx;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.zero88.jooqx.adapter.RowConverterStrategy;
import io.github.zero88.jooqx.adapter.SelectStrategy;
import io.vertx.codegen.annotations.GenIgnore;

/**
 * Represents for a collector that collects {@code Vert.x SQL result} to an expectation output
 *
 * @param <RS> Type of Vertx SQL result set
 * @see LegacySQLCollector
 * @see JooqxResultCollector
 * @see JooqxBatchCollector
 * @since 1.0.0
 */
public interface SQLResultCollector<RS> {

    @GenIgnore
    Logger LOGGER = LoggerFactory.getLogger(SQLResultCollector.class);

    @GenIgnore
    default void warnManyResult(boolean tooManyResults, @NotNull SelectStrategy strategy) {
        if (tooManyResults) {
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
    @NotNull <T, R> List<R> collect(@NotNull RS resultSet, @NotNull RowConverterStrategy<T, R> strategy);

}
