package io.github.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
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
     */
    default void sql(@NotNull String statement, @NotNull Handler<AsyncResult<Integer>> handler) {
        sql(statement).onComplete(handler);
    }

    /**
     * Like {@link #sql(String, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param statement the plain SQL statement without result
     * @return a {@code Future} of the asynchronous result
     */
    Future<Integer> sql(@NotNull String statement);

    /**
     * Execute the plain SQL statement with results (e.g: SELECT, etc...)
     *
     * @param sqlFunction the plain SQL function products a plain SQL statement with results
     * @param adapter     the result adapter
     * @param handler     async result handler
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void sqlQuery(@NotNull Function<DSLContext, String> sqlFunction,
                                 @NotNull SQLResultAdapter<T, R> adapter,
                                 @NotNull Handler<AsyncResult<@Nullable R>> handler) {
        sqlQuery(sqlFunction, adapter).onComplete(handler);
    }

    /**
     * Like {@link #sqlQuery(Function, SQLResultAdapter, Handler)} but returns a {@code Future} of the asynchronous
     * result
     *
     * @param sqlFunction the plain SQL function products a plain SQL statement with results
     * @param adapter     the result adapter
     * @return a {@code Future} of the asynchronous result
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> Future<@Nullable R> sqlQuery(@NotNull Function<DSLContext, String> sqlFunction,
                                                @NotNull SQLResultAdapter<T, R> adapter) {
        return sqlQuery(sqlFunction.apply(dsl()), adapter);
    }

    /**
     * Execute the plain SQL statement with results (e.g: SELECT, etc...)
     *
     * @param statement the plain SQL statement with results
     * @param adapter   the result adapter
     * @param handler   async result handler
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void sqlQuery(@NotNull String statement, @NotNull SQLResultAdapter<T, R> adapter,
                                 @NotNull Handler<AsyncResult<@Nullable R>> handler) {
        sqlQuery(statement, adapter).onComplete(handler);
    }

    /**
     * Like {@link #sqlQuery(String, SQLResultAdapter, Handler)} but returns a {@code Future} of the asynchronous
     * result
     *
     * @param statement the plain SQL statement with results
     * @return a {@code Future} of the asynchronous result
     */
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T, R> Future<@Nullable R> sqlQuery(@NotNull String statement, @NotNull SQLResultAdapter<T, R> adapter);

}
