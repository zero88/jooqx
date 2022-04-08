package io.zero88.jooqx;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.Vertx;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

interface SQLExecutorSetting<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>>
    extends JooqDSLProvider {

    /**
     * Defines Vertx
     *
     * @return vertx
     */
    @NotNull Vertx vertx();

    /**
     * Defines SQL client
     *
     * @return sql client
     */
    @NotNull S sqlClient();

    /**
     * Defines prepared query
     *
     * @return prepared query
     * @see SQLPreparedQuery
     */
    PQ preparedQuery();

    /**
     * Defines result collector depends on result set
     *
     * @return result collector
     * @see SQLResultCollector
     */
    RC resultCollector();

    /**
     * Defines an error converter that rethrows a uniform exception by {@link SQLErrorConverter#reThrowError(Throwable)}
     * if any error in execution time
     *
     * @return error handler
     * @apiNote Default is {@link SQLErrorConverter#DEFAULT} that keeps error as it is
     * @see SQLErrorConverter
     */
    SQLErrorConverter errorConverter();

    /**
     * Defines global data type mapper registry
     *
     * @return registry
     * @see DataTypeMapperRegistry
     */
    DataTypeMapperRegistry typeMapperRegistry();

}
