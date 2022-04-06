package io.github.zero88.jooqx;

import io.vertx.core.Vertx;

public interface ExampleReactive {

    void rx(Vertx vertx);

    void rxBuilder(io.vertx.reactivex.core.Vertx vertx);

}
