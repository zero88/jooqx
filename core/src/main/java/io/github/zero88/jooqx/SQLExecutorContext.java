package io.github.zero88.jooqx;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.core.Vertx;

interface SQLExecutorContext<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>>
    extends JooqDSLProvider {

    @NotNull Vertx vertx();

    @NotNull S sqlClient();

    PQ preparedQuery();

    RC resultCollector();

    SQLErrorConverter errorConverter();

    DataTypeMapperRegistry typeMapperRegistry();

}
