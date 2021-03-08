package io.github.zero88.jooq.vertx;

import io.vertx.junit5.VertxTestContext;

public interface HasSqlClient<S> {

    S sqlClient();

    void closeClient(VertxTestContext context);

}
