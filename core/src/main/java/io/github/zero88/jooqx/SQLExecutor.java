package io.github.zero88.jooqx;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.core.Vertx;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx SQL client} connection
 *
 * @param <S>  Type of Vertx SQL client
 * @param <B>  Type of Vertx SQL bind value holder
 * @param <RS> Type of Vertx SQL result set holder
 * @param <PQ> Type of SQL prepare query
 * @param <RC> Type of SQL result set collector
 * @see JooqDSLProvider
 * @see SQLBatchExecutor
 * @see SQLQueryExecutor
 * @see SQLDDLExecutor
 * @see SQLPlainExecutor
 * @since 1.0.0
 */
public interface SQLExecutor<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>>
    extends SQLExecutorContext<S, B, PQ, RS, RC>, SQLQueryExecutor, SQLBatchExecutor, SQLDDLExecutor, SQLPlainExecutor {

    /**
     * Defines Vertx
     *
     * @return vertx
     */
    @NotNull Vertx vertx();

    /**
     * Defines Vertx SQL client
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

    /**
     * Open transaction executor
     *
     * @param <E> Type of VertxJooqExecutor
     * @return transaction executor
     * @see SQLTxExecutor
     */
    @NotNull <E extends SQLExecutor<S, B, PQ, RS, RC>> SQLTxExecutor<S, B, PQ, RS, RC, E> transaction();

}
