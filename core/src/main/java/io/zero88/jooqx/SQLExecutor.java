package io.zero88.jooqx;

import org.jetbrains.annotations.NotNull;

import io.vertx.ext.sql.SQLClient;
import io.vertx.sqlclient.SqlClient;

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
    extends SQLExecutorSetting<S, B, PQ, RS, RC>, SQLQueryExecutor, SQLBatchExecutor, SQLDDLExecutor {

    /**
     * Open transaction executor
     *
     * @param <E> Type of VertxJooqExecutor
     * @return transaction executor
     * @see SQLTxExecutor
     */
    @NotNull <E extends SQLExecutor<S, B, PQ, RS, RC>> SQLTxExecutor<S, B, PQ, RS, RC, E> transaction();

}
