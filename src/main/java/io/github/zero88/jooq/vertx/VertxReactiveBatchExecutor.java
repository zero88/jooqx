package io.github.zero88.jooq.vertx;

import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.converter.ResultBatchConverter;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import lombok.NonNull;

/**
 * A reactive batch is able to returning result
 */
public interface VertxReactiveBatchExecutor {

    <Q extends Query, T extends TableLike<?>, R extends Record> void batchExecute(@NonNull Q query,
                                                                                  @NonNull BindBatchValues bindBatchValues,
                                                                                  @NonNull ResultBatchConverter<RowSet<Row>, T> rsConverter,
                                                                                  @NonNull Table<R> table,
                                                                                  @NonNull Handler<AsyncResult<BatchReturningResult<R>>> handler);

    <Q extends Query, T extends TableLike<?>, R extends Record> void batchExecute(@NonNull Q query,
                                                                                  @NonNull BindBatchValues bindBatchValues,
                                                                                  @NonNull ResultSetConverter<RowSet<Row>, T> rsConverter,
                                                                                  @NonNull R record,
                                                                                  @NonNull Handler<AsyncResult<BatchReturningResult<R>>> handler);

    <Q extends Query, T extends TableLike<?>, R> void batchExecute(@NonNull Q query,
                                                                   @NonNull BindBatchValues bindBatchValues,
                                                                   @NonNull ResultSetConverter<RowSet<Row>, T> rsConverter,
                                                                   @NonNull Class<R> recordClass,
                                                                   @NonNull Handler<AsyncResult<BatchReturningResult<R>>> handler);

}
