package io.github.zero88.jooq.vertx;

import io.vertx.core.Vertx;

public interface JooqExecutorCreation<S, P, R, BS extends BatchResult, E extends VertxJooqExecutor<S, P, R, BS>>
    extends HasSqlClient<S> {

    E createExecutor(Vertx vertx, JooqSql<?> jooq);

}
