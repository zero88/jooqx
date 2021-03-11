package io.zero88.jooqx;

import java.util.function.Function;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import lombok.NonNull;

/**
 * Represents for transaction executor
 *
 * @param <S>  Type of Vertx SQL client
 * @param <P>  Type of
 * @param <RS>
 * @param <E>
 */
public interface SQLTxExecutor<S, P, RS, E extends SQLExecutor<S, P, RS>> {

    /**
     * Run the transactional code
     *
     * @param function transaction function
     * @param handler  handler
     * @param <X>      Type of result
     */
    default <X> void run(@NonNull Function<E, Future<X>> function, @NonNull Handler<AsyncResult<X>> handler) {
        run(function).onComplete(handler);
    }

    /**
     * Like {@link #run(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param function transaction function
     * @param <X>      Type of result
     * @return a {@code Future} of the asynchronous result
     */
    <X> Future<X> run(@NonNull Function<E, Future<X>> function);

}
