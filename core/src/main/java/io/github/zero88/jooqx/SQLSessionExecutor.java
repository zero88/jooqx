package io.github.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public interface SQLSessionExecutor<S, B, P extends SQLPreparedQuery<B>, C extends SQLResultCollector,
                                       E extends SQLExecutor<S, B, P, C>> {

    /**
     * Perform the session code
     *
     * @param sessionFn session function
     * @param handler   handler
     * @param <X>       Type of result
     */
    <X> void perform(@NotNull Function<E, Future<X>> sessionFn, @NotNull Handler<AsyncResult<X>> handler);

    /**
     * Like {@link #perform(Function, Handler)} but returns a {@code Future} of the asynchronous result
     *
     * @param sessionFn session function
     * @param <X>       Type of result
     * @return a {@code Future} of the asynchronous result
     */
    <X> Future<X> perform(@NotNull Function<E, Future<X>> sessionFn);

}
