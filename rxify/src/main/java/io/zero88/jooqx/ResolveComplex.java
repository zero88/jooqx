package io.zero88.jooqx;

import java.util.function.Function;

import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

@VertxGen
public interface ResolveComplex extends Complex<Another, Integer, String, WithType<Integer, String>> {

    @Override
    default <X> void run(Function<WithType<Integer, String>, Future<X>> function,
                         Handler<AsyncResult<@Nullable X>> handler) {

    }

    @Override
    <X> Future<X> run(Function<WithType<Integer, String>, Future<@Nullable X>> function);

    @Override
    Another alo();

}
