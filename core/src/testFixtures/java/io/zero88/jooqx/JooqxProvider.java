package io.zero88.jooqx;

import io.vertx.core.Vertx;

public interface JooqxProvider<S, B, P extends SQLPreparedQuery<B>, R, C extends SQLResultCollector<R>,
                                  E extends SQLExecutor<S, B, P, R, C>>
    extends ErrorConverterCreator, TypeMapperRegistryCreator {

    E createExecutor(Vertx vertx, JooqDSLProvider dslProvider, S sqlClient);

    P createPreparedQuery();

}
