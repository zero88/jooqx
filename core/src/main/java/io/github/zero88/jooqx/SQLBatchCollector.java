package io.github.zero88.jooqx;

import org.jetbrains.annotations.NotNull;

/**
 * Represents for a collector that collects {@code Vert.x SQL batch result} to an expectation output
 *
 * @param <BR> Type of Vertx SQL batch result
 * @see SQLResultCollector
 * @since 1.0.0
 */
public interface SQLBatchCollector<BR> {

    /**
     * Compute Batch Result size
     *
     * @param batchResult batch result
     * @return result size
     */
    int batchResultSize(@NotNull BR batchResult);

    /**
     * Collect the Vert.x batch result to the informative batch result
     *
     * @param bindValues  the bind batch values
     * @param batchResult the Vert.x batch result
     * @return the batch result
     * @see BatchResult
     */
    default @NotNull BatchResult batchResult(@NotNull BindBatchValues bindValues, @NotNull BR batchResult) {
        return BatchResult.create(bindValues.size(), batchResultSize(batchResult));
    }

}
