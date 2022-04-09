package io.zero88.jooqx;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.Vertx;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

interface SQLExecutorContext<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>>
    extends JooqDSLProvider {

    @NotNull Vertx vertx();

    @NotNull S sqlClient();

    PQ preparedQuery();

    RC resultCollector();

    SQLErrorConverter errorConverter();

    DataTypeMapperRegistry typeMapperRegistry();

}
