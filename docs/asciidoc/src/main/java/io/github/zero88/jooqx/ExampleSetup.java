package io.github.zero88.jooqx;

import io.vertx.core.Vertx;

public interface ExampleSetup {

    void initJDBCClient(Vertx vertx);

    void initJDBCPool(Vertx vertx);

    void initPgClient(Vertx vertx);

    void initPgPool(Vertx vertx);

}
