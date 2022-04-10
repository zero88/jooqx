package io.zero88.jooqx;

import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx reactive SQL connection}
 *
 * @param <S> Type of SqlClient, can be {@code SqlConnection} or {@code Pool}
 * @see SqlConnection
 * @see Pool
 * @since 2.0.0
 */
public interface JooqxBase<S extends SqlClient>
    extends SQLExecutor<S, Tuple, JooqxPreparedQuery, RowSet<Row>, JooqxResultCollector>, SQLBatchResultExecutor {

}
