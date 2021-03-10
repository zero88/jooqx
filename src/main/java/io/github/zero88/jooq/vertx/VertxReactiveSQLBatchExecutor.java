package io.github.zero88.jooq.vertx;

import org.jooq.Query;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.adapter.SelectListResultAdapter;
import io.github.zero88.jooq.vertx.converter.ReactiveSQLResultBatchConverter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
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
public interface VertxReactiveSQLBatchExecutor extends SQLBatchExecutor {

    /**
     * Batch execute
     *
     * @param query           jOOQ query
     * @param bindBatchValues bind batch values
     * @param adapter         result adapter
     * @param handler         async result handler
     * @param <Q>             type of jOOQ
     * @param <T>             type of jOOQ table
     * @param <R>             type of record
     * @see BindBatchValues
     * @see SelectListResultAdapter
     * @see BatchReturningResult
     */
    default <Q extends Query, T extends TableLike<?>, R> void batch(@NonNull Q query,
                                                                    @NonNull BindBatchValues bindBatchValues,
                                                                    @NonNull SelectListResultAdapter<RowSet<Row>,
                                                                                                        ReactiveSQLResultBatchConverter, T, R> adapter,
                                                                    @NonNull Handler<AsyncResult<BatchReturningResult<R>>> handler) {
        batch(query, bindBatchValues, adapter).onComplete(handler);
    }

    /**
     * Like {@link #batch(Query, BindBatchValues, SelectListResultAdapter, Handler)} but returns a {@code Future} of the
     * asynchronous result
     *
     * @param query           jOOQ query
     * @param bindBatchValues bind batch values
     * @param adapter         result adapter
     * @param <Q>             type of jOOQ
     * @param <T>             type of jOOQ table
     * @param <R>             type of record
     * @return a {@code Future} of the asynchronous result
     * @see BindBatchValues
     * @see SelectListResultAdapter
     * @see BatchReturningResult
     */
    <Q extends Query, T extends TableLike<?>, R> Future<BatchReturningResult<R>> batch(@NonNull Q query,
                                                                                       @NonNull BindBatchValues bindBatchValues,
                                                                                       @NonNull SelectListResultAdapter<RowSet<Row>, ReactiveSQLResultBatchConverter, T, R> adapter);

}
