package io.github.zero88.jooq.vertx;

import io.vertx.core.Vertx;

public interface JooqExecutorProvider<S, P, R, E extends VertxJooqExecutor<S, P, R>> {

    E createExecutor(Vertx vertx, JooqDSLProvider dslProvider, S sqlClient);

    QueryHelper<P> createQueryHelper();

    default SqlErrorConverter<? extends Throwable, ? extends RuntimeException> createErrorConverter() {
        return SqlErrorConverter.DEFAULT;
    }

}
