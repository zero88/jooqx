package io.zero88.jooqx;

import org.jooq.DSLContext;
import org.jooq.Query;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.SqlConnection;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.NonNull;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx reactive SQL client} connection
 *
 * @see SqlConnection
 * @since 1.0.0
 */
@VertxGen
public interface ReactiveConnJooqx extends ReactiveJooqx<SqlConnection> {

    @GenIgnore
    static ReactiveJooqxBuilder<SqlConnection> connBuilder() {
        return ReactiveJooqx.builder();
    }

    @Override
    @NonNull Vertx vertx();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull DSLContext dsl();

    @Override
    @NonNull SqlConnection sqlClient();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull ReactiveSQLPreparedQuery preparedQuery();

    @Override
    @NonNull ReactiveSQLResultCollector resultCollector();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull SQLErrorConverter errorConverter();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull DataTypeMapperRegistry typeMapperRegistry();

    @Override
    @SuppressWarnings("unchecked")
    @NonNull ReactiveJooqxTx transaction();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T, R> Future<R> execute(@NonNull Query query, @NonNull SQLResultAdapter<T, R> adapter);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void execute(@NonNull Query query, @NonNull SQLResultAdapter<T, R> adapter,
                                @NonNull Handler<AsyncResult<@Nullable R>> handler) {
        ReactiveJooqx.super.execute(query, adapter, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
                       @NonNull Handler<AsyncResult<BatchResult>> handler) {
        ReactiveJooqx.super.batch(query, bindBatchValues, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<BatchResult> batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T, R> Future<BatchReturningResult<R>> batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
                                                 @NonNull SQLResultAdapter.SQLResultListAdapter<T, R> adapter);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
                              @NonNull SQLResultAdapter.SQLResultListAdapter<T, R> adapter,
                              @NonNull Handler<AsyncResult<BatchReturningResult<R>>> handler) {
        ReactiveJooqx.super.batch(query, bindBatchValues, adapter, handler);
    }

}