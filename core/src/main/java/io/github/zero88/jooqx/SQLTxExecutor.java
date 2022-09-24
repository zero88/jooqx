package io.github.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * Represents for SQL transaction executor
 *
 * @param <S> Type of Vertx SQL client
 * @param <B> Type of Vertx query param holder
 * @param <P> Type of SQL prepare query
 * @param <C> Type of SQL result collector
 * @param <E> Type of jOOQ.x executor
 * @since 1.0.0
 */
public interface SQLTxExecutor<S, B, P extends SQLPreparedQuery<B>, C extends SQLResultCollector,
                                  E extends SQLExecutor<S, B, P, C>> {

    /**
     * Run the transactional code
     *
     * @param transactionFn transaction function
     * @param handler       handler
     * @param <X>           Type of result
     */
    <X> void run(@NotNull Function<E, Future<X>> transactionFn, @NotNull Handler<AsyncResult<X>> handler);

    /**
     * Like {@link #run(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param transactionFn transaction function
     * @param <X>           Type of result
     * @return a {@code Future} of the asynchronous result
     */
    <X> Future<X> run(@NotNull Function<E, Future<X>> transactionFn);

}
