package io.zero88.jooqx;

import java.util.function.Function;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public abstract class ComplexRxImpl<S extends Another, P, RS, E extends WithType<P, RS>>
    implements Complex<S, P, RS, E> {

    @Override
    public <X> void run(Function<E, Future<X>> function, Handler<AsyncResult<@Nullable X>> handler) {
        run(function).onComplete(handler);
    }

    @Override
    public abstract  <X> Future<X> run(Function<E, Future<@Nullable X>> function);

}
