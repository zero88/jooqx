package io.zero88.jooqx;

import org.jooq.Query;
import org.jooq.TableLike;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.zero88.jooqx.adapter.SelectListResultAdapter;

import lombok.NonNull;

/**
 * The {@code reactive sql executor} that is adapted for {@link SqlClient} to execute batch SQL command and able to
 * return the number of succeed row and row detail
 *
 * @since 1.0.0
 */
public interface ReactiveSQLBatchExecutor extends SQLBatchExecutor {

    /**
     * Batch execute
     *
     * @param <T>             type of jOOQ table
     * @param <R>             type of record
     * @param query           jOOQ query
     * @param bindBatchValues bind batch values
     * @param adapter         result adapter
     * @param handler         async result handler
     * @see BindBatchValues
     * @see SelectListResultAdapter
     * @see BatchReturningResult
     */
    default <T extends TableLike<?>, R> void batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
                                                   @NonNull SelectListResultAdapter<RowSet<Row>,
                                                                                       ReactiveSQLResultBatchConverter, T, R> adapter,
                                                   @NonNull Handler<AsyncResult<BatchReturningResult<R>>> handler) {
        batch(query, bindBatchValues, adapter).onComplete(handler);
    }

    /**
     * Like {@link #batch(Query, BindBatchValues, SelectListResultAdapter, Handler)} but returns a {@code Future} of the
     * asynchronous result
     *
     * @param <T>             type of jOOQ table
     * @param <R>             type of record
     * @param query           jOOQ query
     * @param bindBatchValues bind batch values
     * @param adapter         result adapter
     * @return a {@code Future} of the asynchronous result
     * @see BindBatchValues
     * @see SelectListResultAdapter
     * @see BatchReturningResult
     */
    <T extends TableLike<?>, R> Future<BatchReturningResult<R>> batch(@NonNull Query query,
                                                                      @NonNull BindBatchValues bindBatchValues,
                                                                      @NonNull SelectListResultAdapter<RowSet<Row>,
                                                                                                          ReactiveSQLResultBatchConverter, T, R> adapter);

}
