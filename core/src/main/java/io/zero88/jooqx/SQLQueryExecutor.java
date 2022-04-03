package io.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.TableLike;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.zero88.jooqx.adapter.SQLResultAdapter;

/**
 * Represents for a {@code query executor} that executes SQL command
 *
 * @since 1.1.0
 */
public interface SQLQueryExecutor extends JooqDSLProvider {

    /**
     * Execute {@code jOOQ query} then return async result
     *
     * @param <T>           type of jOOQ TableLike
     * @param <R>           type of expectation result
     * @param queryFunction the jOOQ query function
     * @param resultAdapter the result adapter
     * @param handler       the async result handler
     * @see Query
     * @see TableLike
     * @see SQLResultAdapter
     * @since 1.1.0
     */
    default <T, R> void execute(@NotNull Function<DSLContext, Query> queryFunction,
                                @NotNull SQLResultAdapter<T, R> resultAdapter,
                                @NotNull Handler<AsyncResult<@Nullable R>> handler) {
        execute(queryFunction, resultAdapter).onComplete(handler);
    }

    /**
     * Like {@link #execute(Function, SQLResultAdapter, Handler)} but returns a {@code Future} of the asynchronous
     * result
     *
     * @param <T>           type of jOOQ TableLike
     * @param <R>           type of expectation result
     * @param queryFunction the jOOQ query function
     * @param resultAdapter the result adapter
     * @return a {@code Future} of the asynchronous result
     * @since 1.1.0
     */
    default <T, R> Future<@Nullable R> execute(@NotNull Function<DSLContext, Query> queryFunction,
                                               @NotNull SQLResultAdapter<T, R> resultAdapter) {
        return execute(queryFunction.apply(dsl()), resultAdapter);
    }

    /**
     * Execute {@code jOOQ query} then return async result
     *
     * @param <T>           type of jOOQ TableLike
     * @param <R>           type of expectation result
     * @param query         the jOOQ query
     * @param resultAdapter the result adapter
     * @param handler       the async result handler
     * @see Query
     * @see TableLike
     * @see SQLResultAdapter
     */
    default <T, R> void execute(@NotNull Query query, @NotNull SQLResultAdapter<T, R> resultAdapter,
                                @NotNull Handler<AsyncResult<@Nullable R>> handler) {
        execute(query, resultAdapter).onComplete(handler);
    }

    /**
     * Like {@link #execute(Query, SQLResultAdapter, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param <T>           type of jOOQ TableLike
     * @param <R>           type of expectation result
     * @param query         the jOOQ query
     * @param resultAdapter the result adapter
     * @return a {@code Future} of the asynchronous result
     */
    <T, R> Future<@Nullable R> execute(@NotNull Query query, @NotNull SQLResultAdapter<T, R> resultAdapter);

}
