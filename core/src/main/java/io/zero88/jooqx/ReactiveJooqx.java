package io.zero88.jooqx;

import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.TableLike;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.adapter.SelectList;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

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
//@VertxGen
public interface ReactiveJooqx<S extends SqlClient>
    extends SQLExecutor<S, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>, ReactiveSQLResultCollector>,
            SQLTxExecutor<S, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>, ReactiveSQLResultCollector,
                             ReactiveJooqx<S>>,
            ReactiveSQLBatchExecutor {

    @GenIgnore
    static <S extends SqlClient> ReactiveJooqxImpl.ReactiveJooqxBuilder<S> builder() {
        return ReactiveJooqxImpl.builder();
    }

    @Override
    @NonNull Vertx vertx();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull DSLContext dsl();

    @Override
    @NonNull S sqlClient();

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
    default @NonNull ReactiveJooqx<S> transaction() {
        return this;
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T extends TableLike<?>, R> Future<R> execute(@NonNull Query query, @NonNull SQLResultAdapter<T, R> adapter);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T extends TableLike<?>, R> void execute(@NonNull Query query, @NonNull SQLResultAdapter<T, R> adapter,
                                                     @NonNull Handler<AsyncResult<@Nullable R>> handler) {
        SQLExecutor.super.execute(query, adapter, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
                       @NonNull Handler<AsyncResult<BatchResult>> handler) {
        SQLExecutor.super.batch(query, bindBatchValues, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<BatchResult> batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T extends TableLike<?>, R extends Record, O> Future<BatchReturningResult<O>> batch(@NonNull Query query,
                                                                                        @NonNull BindBatchValues bindBatchValues,
                                                                                        @NonNull SelectList<T, R, O> adapter);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T extends TableLike<?>, R extends Record, O> void batch(@NonNull Query query,
                                                                     @NonNull BindBatchValues bindBatchValues,
                                                                     @NonNull SelectList<T, R, O> adapter,
                                                                     @NonNull Handler<AsyncResult<BatchReturningResult<O>>> handler) {
        ReactiveSQLBatchExecutor.super.batch(query, bindBatchValues, adapter, handler);
    }

    @Override
    default <X> void run(@NonNull Function<ReactiveJooqx<S>, Future<X>> function,
                         @NonNull Handler<AsyncResult<X>> handler) {
        SQLTxExecutor.super.run(function, handler);
    }

    @Override
    <X> Future<X> run(@NonNull Function<ReactiveJooqx<S>, Future<X>> function);

}
