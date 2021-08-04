package io.zero88.jooqx;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.JooqxSQLImpl.JooqxConnImpl;
import io.zero88.jooqx.JooqxSQLImpl.JooqxPoolImpl;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx reactive SQL connection}
 *
 * @param <S> Type of SqlClient, can be {@code SqlConnection} or {@code Pool}
 * @see SqlConnection
 * @see Pool
 * @since 1.1.0
 */
public interface JooqxBase<S extends SqlClient>
    extends SQLExecutor<S, Tuple, JooqxPreparedQuery, RowSet<Row>, JooqxResultCollector>, JooqxBatchExecutor {

    static <S extends SqlClient> JooqxBaseBuilder<S, JooqxBase<S>> baseBuilder() {
        return new JooqxBaseBuilder<>();
    }

    @GenIgnore
    class JooqxBaseBuilder<S extends SqlClient, X extends JooqxBase<S>>
        extends SQLExecutorBuilder<S, Tuple, JooqxPreparedQuery, RowSet<Row>, JooqxResultCollector, X> {

        @Override
        @SuppressWarnings("unchecked")
        public X build() {
            if (sqlClient() instanceof SqlConnection) {
                return (X) new JooqxConnImpl((JooqxBaseBuilder<SqlConnection, JooqxBase<SqlConnection>>) this);
            }
            if (sqlClient() instanceof Pool) {
                return (X) new JooqxPoolImpl((JooqxBaseBuilder<Pool, JooqxBase<Pool>>) this);
            }
            throw new UnsupportedOperationException("Unsupported to SQL client: [" + sqlClient().getClass() + "]");
        }

    }

}
