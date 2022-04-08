package io.github.zero88.jooqx;

import io.vertx.core.Vertx;

public interface ExampleAdvancedSQL {

    void batch(Vertx vertx);

    void procedure(Vertx vertx);

    void transaction(Vertx vertx);

}
