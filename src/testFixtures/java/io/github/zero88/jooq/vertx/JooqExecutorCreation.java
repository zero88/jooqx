package io.github.zero88.jooq.vertx;

import io.vertx.core.Vertx;

public interface JooqExecutorCreation<S, P, R, E extends VertxJooqExecutor<S, P, R>> extends HasSqlClient<S> {

    E createExecutor(Vertx vertx, JooqSql<?> jooq);

    QueryHelper<P> createQueryHelper();

    default SqlErrorMaker<? extends Throwable, ? extends RuntimeException> createErrorMaker() {
        return SqlErrorMaker.DEFAULT;
    }

}
