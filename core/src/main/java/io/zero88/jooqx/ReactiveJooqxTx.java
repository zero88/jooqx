package io.zero88.jooqx;

import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.Query;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.NonNull;

/**
 * Represents for a reactive SQL transaction executor
 *
 * @since 1.0.0
 */
@VertxGen
public interface ReactiveJooqxTx extends ReactiveConnJooqx,
                                         SQLTxExecutor<SqlConnection, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>,
                                                          ReactiveSQLResultCollector, ReactiveJooqxTx> {

//    @Override
//    @NonNull Vertx vertx();
//
//    @Override
//    @GenIgnore(GenIgnore.PERMITTED_TYPE)
//    @NonNull DSLContext dsl();
//
//    @Override
//    @NonNull SqlConnection sqlClient();
//
//    @Override
//    @GenIgnore(GenIgnore.PERMITTED_TYPE)
//    @NonNull ReactiveSQLPreparedQuery preparedQuery();
//
//    @Override
//    @GenIgnore(GenIgnore.PERMITTED_TYPE)
//    @NonNull ReactiveSQLResultCollector resultCollector();
//
//    @Override
//    @GenIgnore(GenIgnore.PERMITTED_TYPE)
//    @NonNull SQLErrorConverter errorConverter();
//
//    @Override
//    @GenIgnore(GenIgnore.PERMITTED_TYPE)
//    @NonNull DataTypeMapperRegistry typeMapperRegistry();

    @Override
    default @NonNull ReactiveJooqxTx transaction() {
        return this;
    }

//    @Override
//    @GenIgnore(GenIgnore.PERMITTED_TYPE)
//    default <T, R> void execute(@NonNull Query query, @NonNull SQLResultAdapter<T, R> resultAdapter,
//                                @NonNull Handler<AsyncResult<@Nullable R>> handler) {
//        ReactiveConnJooqx.super.execute(query, resultAdapter, handler);
//    }
//
//    @Override
//    @GenIgnore(GenIgnore.PERMITTED_TYPE)
//    <T, R> Future<@Nullable R> execute(@NonNull Query query, @NonNull SQLResultAdapter<T, R> resultAdapter);
//
//    @Override
//    @GenIgnore(GenIgnore.PERMITTED_TYPE)
//    Future<BatchResult> batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues);
//
//    @Override
//    @GenIgnore(GenIgnore.PERMITTED_TYPE)
//    default void batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
//                       @NonNull Handler<AsyncResult<BatchResult>> handler) {
//        ReactiveConnJooqx.super.batch(query, bindBatchValues, handler);
//    }
//
//    @Override
//    @GenIgnore(GenIgnore.PERMITTED_TYPE)
//    <T, R> Future<BatchReturningResult<R>> batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
//                                                 @NonNull SQLResultAdapter.SQLResultListAdapter<T, R> adapter);
//
//    @Override
//    @GenIgnore(GenIgnore.PERMITTED_TYPE)
//    default <T, R> void batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
//                              @NonNull SQLResultAdapter.SQLResultListAdapter<T, R> adapter,
//                              @NonNull Handler<AsyncResult<BatchReturningResult<R>>> handler) {
//        ReactiveConnJooqx.super.batch(query, bindBatchValues, adapter, handler);
//    }

    @Override
    default <X> void run(@NonNull Function<ReactiveJooqxTx, Future<X>> function,
                         @NonNull Handler<AsyncResult<X>> handler) {
        SQLTxExecutor.super.run(function, handler);
    }

    @Override
    <X> Future<X> run(@NonNull Function<ReactiveJooqxTx, Future<X>> function);

}
