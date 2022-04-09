package io.zero88.jooqx;

import org.jetbrains.annotations.NotNull;

/**
 * Represents for a collector that collects {@code Vert.x SQL batch result} to an expectation output
 *
 * @param <RS> Type of Vertx SQL result set
 * @param <BR> Type of Vertx SQL batch result
 * @see SQLResultCollector
 * @since 1.0.0
 */
interface SQLBatchCollector<RS, BR> extends SQLResultCollector<RS> {

    /**
     * Compute Batch Result size
     *
     * @param batchResult batch result
     * @return result size
     */
    int batchResultSize(@NotNull BR batchResult);

}
