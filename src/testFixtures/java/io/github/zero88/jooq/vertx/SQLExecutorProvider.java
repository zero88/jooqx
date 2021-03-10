package io.github.zero88.jooq.vertx;

import io.vertx.core.Vertx;

public interface SQLExecutorProvider<S, P, R, E extends SQLExecutor<S, P, R>> {

    E createExecutor(Vertx vertx, JooqDSLProvider dslProvider, S sqlClient);

    QueryHelper<P> createQueryHelper();

    default SQLErrorConverter<? extends Throwable, ? extends RuntimeException> createErrorConverter() {
        return SQLErrorConverter.DEFAULT;
    }

}
