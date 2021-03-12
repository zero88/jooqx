package io.zero88.jooqx;

import io.vertx.core.Vertx;

public interface JooqxProvider<S, P, R, C extends SQLResultConverter<R>, E extends SQLExecutor<S, P, R, C>>
    extends ErrorConverterCreator {

    E createExecutor(Vertx vertx, JooqDSLProvider dslProvider, S sqlClient);

    SQLPreparedQuery<P> createPreparedQuery();

}
