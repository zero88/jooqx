package io.github.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Represents for a {@code DDL executor} that executes batch SQL command
 *
 * @since 2.0.0
 */
@VertxGen(concrete = false)
public interface SQLPlainExecutor extends JooqDSLProvider {

    /**
     * Execute the plain SQL statement without result (e.g: SET, INSERT, UPDATE, etc...)
     *
     * @param sqlFunction the plain SQL function products a plain SQL statement without result
     * @param handler     async result handler
     * @since 2.0.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void sql(@NotNull Function<DSLContext, String> sqlFunction,
                     @NotNull Handler<AsyncResult<Integer>> handler) {
        sql(sqlFunction).onComplete(handler);
    }

    /**
     * Like {@link #sql(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param sqlFunction the plain SQL function products a plain SQL statement without result
     * @return a {@code Future} of the asynchronous result
     * @since 2.0.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<Integer> sql(@NotNull Function<DSLContext, String> sqlFunction) {
        return sql(sqlFunction.apply(dsl()));
    }

    /**
     * Execute the plain SQL statement without result (e.g: SET, INSERT, UPDATE, etc...)
     *
     * @param statement the plain SQL statement without result
     * @param handler   async result handler
     * @since 2.0.0
     */
    default void sql(@NotNull String statement, @NotNull Handler<AsyncResult<Integer>> handler) {
        sql(statement).onComplete(handler);
    }

    /**
     * Like {@link #sql(String, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param statement the plain SQL statement without result
     * @return a {@code Future} of the asynchronous result
     * @since 2.0.0
     */
    Future<Integer> sql(@NotNull String statement);

    /**
     * Execute the plain SQL statement with results (e.g: SELECT, etc...)
     *
     * @param sqlFunction the plain SQL function products a plain SQL statement with results
     * @param handler     async result handler
     * @since 2.0.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void sqlQuery(@NotNull Function<DSLContext, String> sqlFunction,
                          @NotNull Handler<AsyncResult<Integer>> handler) {
        sqlQuery(sqlFunction).onComplete(handler);
    }

    /**
     * Like {@link #sqlQuery(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param sqlFunction the plain SQL function products a plain SQL statement with results
     * @return a {@code Future} of the asynchronous result
     * @since 2.0.0
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<Integer> sqlQuery(@NotNull Function<DSLContext, String> sqlFunction) {
        return sql(sqlFunction.apply(dsl()));
    }

    /**
     * Execute the plain SQL statement with results (e.g: SELECT, etc...)
     *
     * @param statement the plain SQL statement with results
     * @param handler   async result handler
     * @since 2.0.0
     */
    default void sqlQuery(@NotNull String statement, @NotNull Handler<AsyncResult<@Nullable Integer>> handler) {
        sql(statement).onComplete(handler);
    }

    /**
     * Like {@link #sql(String, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param statement the plain SQL statement with results
     * @return a {@code Future} of the asynchronous result
     * @since 2.0.0
     */
    Future<Integer> sqlQuery(@NotNull String statement);

}
