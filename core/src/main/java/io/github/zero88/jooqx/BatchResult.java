package io.github.zero88.jooqx;

import org.jooq.Query;

import io.vertx.core.Handler;

/**
 * Batch result
 *
 * @see SQLBatchExecutor#batch(Query, BindBatchValues, Handler)
 * @since 1.0.0
 */
public interface BatchResult {

    static BatchResult create(int total, int successes) {
        return new BatchResultImpl(total, successes);
    }

    int getTotal();

    int getSuccesses();

}
