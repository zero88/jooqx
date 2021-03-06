package io.github.zero88.jooq.vertx;

import org.jooq.Query;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.sql.SQLClient;

import lombok.NonNull;

/**
 * The {@code legacy jdbc executor} that is adapted for {@link SQLClient} to execute batch SQL command and return only
 * the number of succeed row
 *
 * @since 1.0.0
 */
public interface BatchLegacyJdbcExecutor {

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
    <Q extends Query> void batchExecute(@NonNull Q query, @NonNull BindBatchValues bindBatchValues,
                                        @NonNull Handler<AsyncResult<BatchResult>> handler);

}
