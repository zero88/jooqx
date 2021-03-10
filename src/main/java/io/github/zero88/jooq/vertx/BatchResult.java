package io.github.zero88.jooq.vertx;

import org.jooq.Query;

import io.vertx.core.Handler;

/**
 * Batch result
 *
 * @see SQLBatchExecutor#batch(Query, BindBatchValues, Handler)
 * @since 1.0.0
 */
public interface BatchResult {

    int getTotal();

    int getSuccesses();

}
