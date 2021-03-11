package io.zero88.jooqx;

import io.vertx.core.Vertx;

public interface SQLExecutorProvider<S, P, R, E extends SQLExecutor<S, P, R>> {

    E createExecutor(Vertx vertx, JooqDSLProvider dslProvider, S sqlClient);

    SQLPreparedQuery<P> createPreparedQuery();

    default SQLErrorConverter<? extends Throwable, ? extends RuntimeException> createErrorConverter() {
        return SQLErrorConverter.DEFAULT;
    }

}
