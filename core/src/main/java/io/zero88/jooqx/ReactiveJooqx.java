package io.zero88.jooqx;

import org.jooq.Query;
import org.jooq.TableLike;

import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.adapter.SQLResultAdapter;

import lombok.NonNull;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx reactive SQL client} connection
 *
 * @param <S> Type of SqlClient, can be {@code SqlConnection} or {@code Pool}
 * @see SqlClient
 * @see SqlConnection
 * @see Pool
 * @see Tuple
 * @see Row
 * @see RowSet
 * @since 1.0.0
 */
public interface ReactiveJooqx<S extends SqlClient>
    extends SQLExecutor<S, Tuple, RowSet<Row>, ReactiveSQLResultCollector>,
            SQLTxExecutor<S, Tuple, RowSet<Row>, ReactiveSQLResultCollector, ReactiveJooqxImpl<S>>,
            ReactiveSQLBatchExecutor {

    static <S extends SqlClient> ReactiveJooqxImpl.ReactiveJooqxImplBuilder<S, ?, ?> builder() {
        return ReactiveJooqxImpl.builder();
    }

    @Override
    @NonNull ReactiveSQLPreparedQuery preparedQuery();

    @Override
    <T extends TableLike<?>, R> Future<R> execute(@NonNull Query query,
                                                  @NonNull SQLResultAdapter<RowSet<Row>, ReactiveSQLResultCollector,
                                                                               T, R> resultAdapter);

}
