package io.zero88.jooqx.examples;

import io.vertx.core.Vertx;

public interface JooqxExample {

    void future(Vertx vertx);

    void rx(Vertx vertx);

    void jsonRecord(Vertx vertx);

    void fetchExists(Vertx vertx);

    void joinQuery(Vertx vertx);

    void batch(Vertx vertx);

    void procedure(Vertx vertx);

    void transaction(Vertx vertx);

    void errorHandler(Vertx vertx);

}
