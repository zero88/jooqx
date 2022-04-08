package io.github.zero88.jooqx;

import io.vertx.core.Vertx;

public interface ExampleResultTransformation {

    void toJsonRecord(Vertx vertx);

    void toRecord(Vertx vertx);

    void toField(Vertx vertx);

}
