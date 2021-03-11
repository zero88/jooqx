package io.zero88.jooqx;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.TableLike;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.sql.SQLClient;
import io.vertx.sqlclient.SqlClient;
import io.zero88.jooqx.adapter.SQLResultAdapter;

import lombok.NonNull;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx SQL client} connection
 *
 * @param <S>  Type of Vertx SQL client. Might be {@link SqlClient} or {@link SQLClient}
 * @param <P>  Type of Vertx SQL bind value holder
 * @param <RS> Type of Vertx SQL Result set holder
 * @see LegacyJooqx
 * @see ReactiveJooqx
 * @see SQLBatchExecutor
 * @since 1.0.0
 */
public interface SQLExecutor<S, P, RS> extends SQLBatchExecutor {

    /**
     * Vertx
     *
     * @return vertx
     */
    @NonNull Vertx vertx();

    /**
     * Define jOOQ DSL context
     *
     * @return DSL context
     * @see DSLContext
     */
    @NonNull DSLContext dsl();

    /**
     * Defines sql client
     *
     * @return sql client
     */
    @NonNull S sqlClient();

    /**
     * Defines prepared query
     *
     * @return prepared query
     * @see SQLPreparedQuery
     */
    @NonNull SQLPreparedQuery<P> preparedQuery();

    /**
     * Defines an error converter that rethrows an uniform exception by
     * {@link SQLErrorConverter#reThrowError(Throwable)}
     * if any error in execution time
     *
     * @return error handler
     * @apiNote Default is {@link SQLErrorConverter#DEFAULT} that keeps error as it is
     * @see SQLErrorConverter
     */
    @NonNull SQLErrorConverter<? extends Throwable, ? extends RuntimeException> errorConverter();

    /**
     * Execute {@code jOOQ query} then return async result
     *
     * @param query         jOOQ query
     * @param resultAdapter a result adapter
     * @param handler       a async result handler
     * @param <Q>           type of jOOQ Query
     * @param <T>           type of jOOQ TableLike
     * @param <C>           type of result set converter
     * @param <R>           type of expectation result
     * @see Query
     * @see TableLike
     * @see SQLResultAdapter
     */
    default <Q extends Query, T extends TableLike<?>, C extends SQLResultSetConverter<RS>, R> void execute(
        @NonNull Q query, @NonNull SQLResultAdapter<RS, C, T, R> resultAdapter,
        @NonNull Handler<AsyncResult<R>> handler) {
        execute(query, resultAdapter).onComplete(handler);
    }

    /**
     * Like {@link #execute(Query, SQLResultAdapter, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param query         jOOQ query
     * @param resultAdapter a result adapter
     * @param <Q>           type of jOOQ Query
     * @param <T>           type of jOOQ TableLike
     * @param <C>           type of result set converter
     * @param <R>           type of expectation result
     * @return a {@code Future} of the asynchronous result
     */
    <Q extends Query, T extends TableLike<?>, C extends SQLResultSetConverter<RS>, R> Future<R> execute(
        @NonNull Q query, @NonNull SQLResultAdapter<RS, C, T, R> resultAdapter);

    /**
     * Open transaction executor
     *
     * @param <E> Type of VertxJooqExecutor
     * @return transaction executor
     * @see SQLTxExecutor
     */
    @NonNull <E extends SQLExecutor<S, P, RS>> SQLTxExecutor<S, P, RS, E> transaction();

}
