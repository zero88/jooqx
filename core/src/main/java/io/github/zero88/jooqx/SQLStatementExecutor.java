package io.github.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.RowCountQuery;
import org.jooq.TableLike;

import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Represents for an {@code executor} that executes SQL statement
 *
 * @since 2.0.0
 */
@VertxGen(concrete = false)
public interface SQLStatementExecutor extends JooqDSLProvider {

    /**
     * Execute {@code jOOQ query} then return async result
     *
     * @param queryFunction the jOOQ query function
     * @param resultAdapter the result adapter
     * @param handler       the async result handler
     * @see Query
     * @see TableLike
     * @see SQLResultAdapter
     * @since 2.0.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void execute(@NotNull Function<DSLContext, Query> queryFunction,
                                @NotNull SQLResultAdapter<T, R> resultAdapter,
                                @NotNull Handler<AsyncResult<@Nullable R>> handler) {
        execute(queryFunction, resultAdapter).onComplete(handler);
    }

    /**
     * Like {@link #execute(Function, SQLResultAdapter, Handler)} but returns a {@code Future} of the asynchronous
     * result
     *
     * @param queryFunction the jOOQ query function
     * @param resultAdapter the result adapter
     * @return a {@code Future} of the asynchronous result
     * @since 2.0.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> Future<@Nullable R> execute(@NotNull Function<DSLContext, Query> queryFunction,
                                               @NotNull SQLResultAdapter<T, R> resultAdapter) {
        return execute(queryFunction.apply(dsl()), resultAdapter);
    }

    /**
     * Execute {@code jOOQ query} then return async result
     *
     * @param query         the jOOQ query
     * @param resultAdapter the result adapter
     * @param handler       the async result handler
     * @see Query
     * @see TableLike
     * @see SQLResultAdapter
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void execute(@NotNull Query query, @NotNull SQLResultAdapter<T, R> resultAdapter,
                                @NotNull Handler<AsyncResult<@Nullable R>> handler) {
        execute(query, resultAdapter).onComplete(handler);
    }

    /**
     * Like {@link #execute(Query, SQLResultAdapter, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param query         the jOOQ query
     * @param resultAdapter the result adapter
     * @return a {@code Future} of the asynchronous result
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T, R> Future<@Nullable R> execute(@NotNull Query query, @NotNull SQLResultAdapter<T, R> resultAdapter);

    /**
     * Execute row count query statement
     *
     * @param rowCountQueryFn A function produces row count query statement
     * @param handler         async result handler
     * @see RowCountQuery
     * @since 2.0.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void execute(@NotNull Function<DSLContext, RowCountQuery> rowCountQueryFn,
                         @NotNull Handler<AsyncResult<Integer>> handler) {
        execute(rowCountQueryFn).onComplete(handler);
    }

    /**
     * Like {@link #execute(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param rowCountQueryFn A function produces row count query statement
     * @return a {@code Future} of the asynchronous result
     * @since 2.0.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<Integer> execute(@NotNull Function<DSLContext, RowCountQuery> rowCountQueryFn) {
        return execute(rowCountQueryFn.apply(dsl()));
    }

    /**
     * Execute row count query statement
     *
     * @param statement a row count query statement
     * @param handler   async result handler
     * @since 2.0.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void execute(@NotNull RowCountQuery statement, @NotNull Handler<AsyncResult<Integer>> handler) {
        execute(statement).onComplete(handler);
    }

    /**
     * Like {@link #execute(RowCountQuery, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param statement a row count query statement
     * @return a {@code Future} of the asynchronous result
     * @since 2.0.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<Integer> execute(@NotNull RowCountQuery statement);

}
