package io.zero88.jooqx;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.ReactiveSQLImpl.ConnectionJooqxImpl;
import io.zero88.jooqx.ReactiveSQLImpl.PoolJooqxImpl;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx reactive SQL connection}
 *
 * @param <S> Type of SqlClient, can be {@code SqlConnection} or {@code Pool}
 * @see SqlConnection
 * @see Pool
 * @since 1.0.0
 */
public interface ReactiveJooqx<S extends SqlClient>
    extends SQLExecutor<S, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>, ReactiveSQLResultCollector>,
            ReactiveSQLBatchExecutor {

    static <S extends SqlClient> ReactiveJooqxBuilder<S> builder() {
        return new ReactiveJooqxBuilder<>();
    }

    @GenIgnore
    class ReactiveJooqxBuilder<S extends SqlClient> extends
                                                    SQLExecutorBuilder<S, Tuple, ReactiveSQLPreparedQuery,
                                                                          RowSet<Row>, ReactiveSQLResultCollector,
                                                                          ReactiveJooqx<S>> {

        @Override
        @SuppressWarnings("unchecked")
        public ReactiveJooqx<S> build() {
            if (sqlClient() instanceof SqlConnection) {
                return (ReactiveJooqx<S>) new ConnectionJooqxImpl(vertx(), dsl(), (SqlConnection) sqlClient(),
                                                                  preparedQuery(), resultCollector(), errorConverter(),
                                                                  typeMapperRegistry());
            }
            if (sqlClient() instanceof Pool) {
                return (ReactiveJooqx<S>) new PoolJooqxImpl(vertx(), dsl(), (Pool) sqlClient(), preparedQuery(),
                                                            resultCollector(), errorConverter(), typeMapperRegistry());
            }
            throw new UnsupportedOperationException("Unsupported to SQL client: [" + sqlClient().getClass() + "]");
        }

    }

}
