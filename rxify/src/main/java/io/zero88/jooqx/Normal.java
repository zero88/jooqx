package io.zero88.jooqx;

import java.util.function.Function;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

@VertxGen
public interface Normal<A> {

    default <X> void run(Function<A, Future<X>> function, Handler<AsyncResult<@Nullable X>> handler) {
        run(function).onComplete(handler);
    }

    /**
     * Like {@link #run(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param function transaction function
     * @param <X>      Type of result
     * @return a {@code Future} of the asynchronous result
     */
    <X> Future<X> run(Function<A, Future<@Nullable X>> function);

}
