package io.github.zero88.jooq.vertx;

import org.jooq.Query;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.adapter.ListResultAdapter;
import io.github.zero88.jooq.vertx.converter.ResultBatchConverter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;

import lombok.NonNull;

/**
 * The {@code reactive sql executor} that is adapted for {@link SqlClient} to execute batch SQL command and able to
 * return the number of succeed row and row detail
 *
 * @since 1.0.0
 */
public interface VertxReactiveBatchExecutor extends VertxBatchExecutor {

    /**
     * Batch execute
     *
     * @param query           jOOQ query
     * @param bindBatchValues bind batch values
     * @param resultAdapter   result adapter
     * @param handler         async result handler
     * @param <Q>             type of jOOQ
     * @param <T>             type of jOOQ table
     * @param <R>             type of record
     * @see BindBatchValues
     * @see ListResultAdapter
     * @see BatchReturningResult
     */
    <Q extends Query, C extends ResultBatchConverter<RowSet<Row>, RowSet<Row>>, T extends TableLike<?>, R> void batchExecute(
        @NonNull Q query, @NonNull BindBatchValues bindBatchValues,
        @NonNull ListResultAdapter<RowSet<Row>, C, T, R> resultAdapter,
        @NonNull Handler<AsyncResult<BatchReturningResult<R>>> handler);

}
