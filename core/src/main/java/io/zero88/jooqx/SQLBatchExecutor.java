package io.zero88.jooqx;

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
     * @see BindBatchValues
     * @see BatchResult
     */
    default void batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
                       @NonNull Handler<AsyncResult<BatchResult>> handler) {
        batch(query, bindBatchValues).onComplete(handler);
    }

    /**
     * Like {@link #batch(Query, BindBatchValues, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param query           query
     * @param bindBatchValues bind batch values
     * @return a {@code Future} of the asynchronous result
     */
    Future<BatchResult> batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues);

}
