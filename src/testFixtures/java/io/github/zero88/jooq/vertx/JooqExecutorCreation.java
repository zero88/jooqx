package io.github.zero88.jooq.vertx;

import io.vertx.core.Vertx;

public interface JooqExecutorCreation<S, R, E extends VertxJooqExecutor<S, R>> extends HasSqlClient<S> {

    E createExecutor(Vertx vertx, JooqSql<?> jooq);

}
