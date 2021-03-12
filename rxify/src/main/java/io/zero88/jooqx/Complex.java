package io.zero88.jooqx;

import java.util.function.Function;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public interface Complex<S extends Another, P, RS, E extends WithType<P, RS>> {

    /**
     * Run the transactional code
     *
     * @param function transaction function
     * @param handler  handler
     * @param <X>      Type of result
     */
    default <X> void run(Function<E, Future<X>> function, Handler<AsyncResult<@Nullable X>> handler) {
        run(function).onComplete(handler);
    }

    /**
     * Like {@link #run(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param function transaction function
     * @param <X>      Type of result
     * @return a {@code Future} of the asynchronous result
     */
    <X> Future<X> run(Function<E, Future<@Nullable X>> function);

    S alo();

}
