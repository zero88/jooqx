package io.zero88.jooqx;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.ReactiveSQLImpl.JooqxConnImpl;
import io.zero88.jooqx.ReactiveSQLImpl.JooqxPoolImpl;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx reactive SQL connection}
 *
 * @param <S> Type of SqlClient, can be {@code SqlConnection} or {@code Pool}
 * @see SqlConnection
 * @see Pool
 * @since 1.0.0
 */
public interface ReactiveJooqxBase<S extends SqlClient>
    extends SQLExecutor<S, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>, ReactiveSQLResultCollector>,
            ReactiveSQLBatchExecutor {

    static <S extends SqlClient> ReAJooqxBBuilder<S, ReactiveJooqxBase<S>> baseBuilder() {
        return new ReAJooqxBBuilder<>();
    }

    @GenIgnore
    class ReAJooqxBBuilder<S extends SqlClient, X extends ReactiveJooqxBase<S>>
        extends SQLExecutorBuilder<S, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>, ReactiveSQLResultCollector, X> {

        @Override
        @SuppressWarnings("unchecked")
        public X build() {
            if (sqlClient() instanceof SqlConnection) {
                return (X) new JooqxConnImpl((ReAJooqxBBuilder<SqlConnection, ReactiveJooqxBase<SqlConnection>>) this);
            }
            if (sqlClient() instanceof Pool) {
                return (X) new JooqxPoolImpl((ReAJooqxBBuilder<Pool, ReactiveJooqxBase<Pool>>) this);
            }
            throw new UnsupportedOperationException("Unsupported to SQL client: [" + sqlClient().getClass() + "]");
        }

    }

}
