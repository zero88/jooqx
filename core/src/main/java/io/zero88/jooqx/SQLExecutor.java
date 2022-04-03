package io.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.vertx.core.Vertx;
import io.vertx.ext.sql.SQLClient;
import io.vertx.sqlclient.SqlClient;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx SQL client} connection
 *
 * @param <S>  Type of Vertx SQL client. Might be {@link SqlClient} or {@link SQLClient}
 * @param <B>  Type of Vertx SQL bind value holder
 * @param <RS> Type of Vertx SQL result set holder
 * @param <PQ> Type of SQL prepare query
 * @param <RC> Type of SQL result set collector
 * @see JooqDSLProvider
 * @see SQLBatchExecutor
 * @see SQLQueryExecutor
 * @see SQLDDLExecutor
 * @since 1.0.0
 */
public interface SQLExecutor<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>>
    extends SQLQueryExecutor, SQLBatchExecutor, SQLDDLExecutor, JooqDSLProvider {

    /**
     * Vertx
     *
     * @return vertx
     */
    @NotNull Vertx vertx();

    /**
     * Defines sql client
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
    @NotNull PQ preparedQuery();

    /**
     * Defines result collector depends on result set
     *
     * @return result collector
     * @see SQLResultCollector
     */
    @NotNull RC resultCollector();

    /**
     * Defines an error converter that rethrows an uniform exception by
     * {@link SQLErrorConverter#reThrowError(Throwable)}
     * if any error in execution time
     *
     * @return error handler
     * @apiNote Default is {@link SQLErrorConverter#DEFAULT} that keeps error as it is
     * @see SQLErrorConverter
     */
    @NotNull SQLErrorConverter errorConverter();

    /**
     * Defines global data type mapper registry
     *
     * @return registry
     * @see DataTypeMapperRegistry
     */
    @NotNull DataTypeMapperRegistry typeMapperRegistry();

    /**
     * Open transaction executor
     *
     * @param <E> Type of VertxJooqExecutor
     * @return transaction executor
     * @see SQLTxExecutor
     */
    @NotNull <E extends SQLExecutor<S, B, PQ, RS, RC>> SQLTxExecutor<S, B, PQ, RS, RC, E> transaction();

    abstract class SQLExecutorBuilder<S, B, P extends SQLPreparedQuery<B>, RS, C extends SQLResultCollector<RS>,
                                         E extends SQLExecutor<S, B, P, RS, C>> {

        private Vertx vertx;
        private DSLContext dsl;
        private S sqlClient;
        private P preparedQuery;
        private C resultCollector;
        private SQLErrorConverter errorConverter;
        private DataTypeMapperRegistry typeMapperRegistry;

        public abstract E build();

        public Vertx vertx() {return vertx;}

        public SQLExecutorBuilder<S, B, P, RS, C, E> vertx(Vertx vertx) {
            this.vertx = vertx;
            return this;
        }

        public DSLContext dsl() {return dsl;}

        public SQLExecutorBuilder<S, B, P, RS, C, E> dsl(DSLContext dsl) {
            this.dsl = dsl;
            return this;
        }

        public S sqlClient() {return sqlClient;}

        public SQLExecutorBuilder<S, B, P, RS, C, E> sqlClient(S sqlClient) {
            this.sqlClient = sqlClient;
            return this;
        }

        public P preparedQuery() {return preparedQuery;}

        public SQLExecutorBuilder<S, B, P, RS, C, E> preparedQuery(P preparedQuery) {
            this.preparedQuery = preparedQuery;
            return this;
        }

        public C resultCollector() {return resultCollector;}

        public SQLExecutorBuilder<S, B, P, RS, C, E> resultCollector(C resultCollector) {
            this.resultCollector = resultCollector;
            return this;
        }

        public SQLErrorConverter errorConverter() {return errorConverter;}

        public SQLExecutorBuilder<S, B, P, RS, C, E> errorConverter(SQLErrorConverter errorConverter) {
            this.errorConverter = errorConverter;
            return this;
        }

        public DataTypeMapperRegistry typeMapperRegistry() {return typeMapperRegistry;}

        public SQLExecutorBuilder<S, B, P, RS, C, E> typeMapperRegistry(DataTypeMapperRegistry typeMapperRegistry) {
            this.typeMapperRegistry = typeMapperRegistry;
            return this;
        }

    }

}
