package io.github.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.core.Vertx;

public interface SQLExecutorBuilder<S, B, PQ extends SQLPreparedQuery<B>, RC extends SQLResultCollector,
                                       E extends SQLExecutorBuilder<S, B, PQ, RC, E>>
    extends SQLExecutorContext<S, B, PQ, RC> {

    /**
     * Set vertx
     *
     * @param vertx vertx
     * @return a reference to this for fluent API
     * @see Vertx
     */
    @NotNull E setVertx(Vertx vertx);

    /**
     * Set DSL context
     *
     * @param dsl dsl context
     * @return a reference to this for fluent API
     * @see DSLContext
     */
    @NotNull E setDSL(DSLContext dsl);

    /**
     * Set SQL client
     *
     * @param sqlClient sql client
     * @return a reference to this for fluent API
     */
    @NotNull E setSqlClient(S sqlClient);

    /**
     * Set Prepare Query
     *
     * @param preparedQuery prepare query
     * @return a reference to this for fluent API
     */
    @NotNull E setPreparedQuery(PQ preparedQuery);

    /**
     * Set Result Collector
     *
     * @param resultCollector a result collector
     * @return a reference to this for fluent API
     */
    @NotNull E setResultCollector(RC resultCollector);

    /**
     * Set Error Converter
     *
     * @param errorConverter an error converter
     * @return a reference to this for fluent API
     * @see SQLErrorConverter
     */
    @NotNull E setErrorConverter(SQLErrorConverter errorConverter);

    /**
     * Set Type Mapper Registry
     *
     * @param typeMapperRegistry a type mapper registry
     * @return a reference to this for fluent API
     * @see DataTypeMapperRegistry
     */
    @NotNull E setTypeMapperRegistry(DataTypeMapperRegistry typeMapperRegistry);

    @NotNull SQLExecutor<S, B, PQ, RC> build();

}
