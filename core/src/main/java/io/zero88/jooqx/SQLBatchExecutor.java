package io.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Query;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Represents for a {@code batch executor} that executes batch SQL command
 *
 * @since 1.0.0
 */
@VertxGen(concrete = false)
public interface SQLBatchExecutor extends JooqDSLProvider {

    /**
     * Batch execute
     *
     * @param queryFunction   query function
     * @param bindBatchValues bind batch values
     * @param handler         async result handler
     * @see BindBatchValues
     * @see BatchResult
     * @since 1.1.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void batch(@NotNull Function<DSLContext, Query> queryFunction, @NotNull BindBatchValues bindBatchValues,
                       @NotNull Handler<AsyncResult<BatchResult>> handler) {
        batch(queryFunction, bindBatchValues).onComplete(handler);
    }

    /**
     * Like {@link #batch(Query, BindBatchValues, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param queryFunction   query function
     * @param bindBatchValues bind batch values
     * @return a {@code Future} of the asynchronous result
     * @since 1.1.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<BatchResult> batch(@NotNull Function<DSLContext, Query> queryFunction,
                                      @NotNull BindBatchValues bindBatchValues) {
        return batch(queryFunction.apply(dsl()), bindBatchValues);
    }

    /**
     * Batch execute
     *
     * @param query           query
     * @param bindBatchValues bind batch values
     * @param handler         async result handler
     * @see BindBatchValues
     * @see BatchResult
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void batch(@NotNull Query query, @NotNull BindBatchValues bindBatchValues,
                       @NotNull Handler<AsyncResult<BatchResult>> handler) {
        batch(query, bindBatchValues).onComplete(handler);
    }

    /**
     * Like {@link #batch(Query, BindBatchValues, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param query           query
     * @param bindBatchValues bind batch values
     * @return a {@code Future} of the asynchronous result
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<BatchResult> batch(@NotNull Query query, @NotNull BindBatchValues bindBatchValues);

}
