package io.zero88.jooqx;

import org.jooq.Query;
import org.jooq.Record;
import org.jooq.TableLike;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.SqlClient;
import io.zero88.jooqx.adapter.SQLResultAdapter.SQLResultListAdapter;
import io.zero88.jooqx.adapter.SelectList;

import lombok.NonNull;

/**
 * The {@code reactive sql executor} that is adapted for {@link SqlClient} to execute batch SQL command and able to
 * return the number of succeed row and row detail
 *
 * @since 1.1.0
 */
public interface JooqxBatchExecutor extends SQLBatchExecutor {

    /**
     * Batch execute
     *
     * @param <T>             type of jOOQ table
     * @param <R>             type of expectation output
     * @param query           jOOQ query
     * @param bindBatchValues bind batch values
     * @param adapter         result adapter
     * @param handler         async result handler
     * @see BindBatchValues
     * @see SelectList
     * @see BatchReturningResult
     * @see TableLike
     * @see Record
     */
    default <T, R> void batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
                              @NonNull SQLResultListAdapter<T, R> adapter,
                              @NonNull Handler<AsyncResult<BatchReturningResult<R>>> handler) {
        batch(query, bindBatchValues, adapter).onComplete(handler);
    }

    /**
     * Like {@link #batch(Query, BindBatchValues, SQLResultListAdapter, Handler)} but returns a {@code Future} of the
     * asynchronous result
     *
     * @param <T>             type of jOOQ table
     * @param <R>             type of expectation result
     * @param query           jOOQ query
     * @param bindBatchValues bind batch values
     * @param adapter         result adapter
     * @return a {@code Future} of the asynchronous result
     * @see BindBatchValues
     * @see SelectList
     * @see BatchReturningResult
     */
    <T, R> Future<BatchReturningResult<R>> batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
                                                 @NonNull SQLResultListAdapter<T, R> adapter);

}
