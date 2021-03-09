package io.github.zero88.jooq.vertx;

import org.jooq.Query;

import io.vertx.core.Handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Batch result
 *
 * @see VertxBatchExecutor#batchExecute(Query, BindBatchValues, Handler)
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor
public class BatchResult {

    private final int total;
    private final int successes;

}
