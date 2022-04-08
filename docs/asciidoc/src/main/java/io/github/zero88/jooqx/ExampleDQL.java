package io.github.zero88.jooqx;

import io.vertx.core.Vertx;

public interface ExampleDQL {

    void fetchExists(Vertx vertx);

    void joinQuery(Vertx vertx);

}
