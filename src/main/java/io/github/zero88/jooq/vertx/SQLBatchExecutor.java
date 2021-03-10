package io.github.zero88.jooq.vertx;

import org.jooq.Query;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import lombok.NonNull;

/**
 * The {@code legacy jdbc executor} that executes batch SQL command and return only the number of succeed row
 *
 * @since 1.0.0
 */
public interface SQLBatchExecutor {

    /**
     * Batch execute
     *
     * @param query           query
     * @param bindBatchValues bind batch values
     * @param handler         async result handler
     * @param <Q>             type of jOOQ query
     * @see BindBatchValues
     * @see BatchResult
     */
    default <Q extends Query> void batch(@NonNull Q query, @NonNull BindBatchValues bindBatchValues,
                                         @NonNull Handler<AsyncResult<BatchResult>> handler) {
        batch(query, bindBatchValues).onComplete(handler);
    }

    /**
     * Like {@link #batch(Query, BindBatchValues, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param query           query
     * @param bindBatchValues bind batch values
     * @param <Q>             type of jOOQ query
     * @return a {@code Future} of the asynchronous result
     */
    <Q extends Query> Future<BatchResult> batch(@NonNull Q query, @NonNull BindBatchValues bindBatchValues);

}
