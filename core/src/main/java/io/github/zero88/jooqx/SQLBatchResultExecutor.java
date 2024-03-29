package io.github.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Query;

import io.github.zero88.jooqx.adapter.SQLResultListAdapter;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.SqlClient;

/**
 * The {@code reactive sql executor} that is adapted for {@link SqlClient} to execute batch SQL command and able to
 * return the number of succeed row and row detail
 *
 * @since 2.0.0
 */
@VertxGen(concrete = false)
public interface SQLBatchResultExecutor extends JooqDSLProvider {

    /**
     * Result batch execute
     *
     * @param queryFunction   query function
     * @param bindBatchValues bind batch values
     * @param handler         async result handler
     * @see BindBatchValues
     * @see SQLResultListAdapter
     * @see BatchReturningResult
     * @since 2.0.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void batchResult(@NotNull Function<DSLContext, Query> queryFunction,
                                    @NotNull BindBatchValues bindBatchValues,
                                    @NotNull SQLResultListAdapter<T, R> adapter,
                                    @NotNull Handler<AsyncResult<BatchReturningResult<R>>> handler) {
        batchResult(queryFunction, bindBatchValues, adapter).onComplete(handler);
    }

    /**
     * Like {@link #batchResult(Function, BindBatchValues, SQLResultListAdapter, Handler)} but returns a {@code Future}
     * of the asynchronous result
     *
     * @param queryFunction   query function
     * @param bindBatchValues bind batch values
     * @return a {@code Future} of the asynchronous result
     * @see BindBatchValues
     * @see SQLResultListAdapter
     * @see BatchReturningResult
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> Future<BatchReturningResult<R>> batchResult(@NotNull Function<DSLContext, Query> queryFunction,
                                                               @NotNull BindBatchValues bindBatchValues,
                                                               @NotNull SQLResultListAdapter<T, R> adapter) {
        return batchResult(queryFunction.apply(dsl()), bindBatchValues, adapter);
    }

    /**
     * Execute batch statements then receive results
     *
     * @param query           jOOQ query
     * @param bindBatchValues bind batch values
     * @param adapter         result adapter
     * @param handler         async result handler
     * @see BindBatchValues
     * @see SQLResultListAdapter
     * @see BatchReturningResult
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void batchResult(@NotNull Query query, @NotNull BindBatchValues bindBatchValues,
                                    @NotNull SQLResultListAdapter<T, R> adapter,
                                    @NotNull Handler<AsyncResult<BatchReturningResult<R>>> handler) {
        batchResult(query, bindBatchValues, adapter).onComplete(handler);
    }

    /**
     * Like {@link #batchResult(Query, BindBatchValues, SQLResultListAdapter, Handler)} but returns a {@code Future} of
     * the asynchronous result
     *
     * @param query           jOOQ query
     * @param bindBatchValues bind batch values
     * @param adapter         result adapter
     * @return a {@code Future} of the asynchronous result
     * @see BindBatchValues
     * @see SQLResultListAdapter
     * @see BatchReturningResult
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T, R> Future<BatchReturningResult<R>> batchResult(@NotNull Query query, @NotNull BindBatchValues bindBatchValues,
                                                       @NotNull SQLResultListAdapter<T, R> adapter);

}
