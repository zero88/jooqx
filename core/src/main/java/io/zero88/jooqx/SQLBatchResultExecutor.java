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
import io.vertx.sqlclient.SqlClient;
import io.zero88.jooqx.adapter.SQLResultAdapter.SQLResultListAdapter;

/**
 * The {@code reactive sql executor} that is adapted for {@link SqlClient} to execute batch SQL command and able to
 * return the number of succeed row and row detail
 *
 * @since 1.1.0
 */
@VertxGen(concrete = false)
public interface SQLBatchResultExecutor extends JooqDSLProvider {

    /**
     * Result batch execute
     *
     * @param <T>             type of jOOQ table
     * @param <R>             type of expectation result
     * @param queryFunction   query function
     * @param bindBatchValues bind batch values
     * @param handler         async result handler
     * @see BindBatchValues
     * @see SQLResultListAdapter
     * @see BatchReturningResult
     * @since 1.1.0
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
     * of the
     * asynchronous result
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
     * Result batch execute
     *
     * @param <T>             type of jOOQ table
     * @param <R>             type of expectation output
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
     * the
     * asynchronous result
     *
     * @param <T>             type of jOOQ table
     * @param <R>             type of expectation result
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
