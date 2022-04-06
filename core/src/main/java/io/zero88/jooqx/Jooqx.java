package io.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jooq.DDLQuery;
import org.jooq.DSLContext;
import org.jooq.Query;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import io.zero88.jooqx.JooqxSQLImpl.JooqxBuilderImpl;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx reactive SQL pool}
 *
 * @see Pool
 * @since 1.1.0
 */
@VertxGen
public interface Jooqx extends JooqxBase<Pool> {

    /**
     * Create a builder
     *
     * @return jooqx builder
     * @see JooqxBuilder
     */
    static JooqxBuilder builder() { return new JooqxBuilderImpl(); }

    @Override
    @NotNull Vertx vertx();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull DSLContext dsl();

    @Override
    @NotNull Pool sqlClient();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull JooqxPreparedQuery preparedQuery();

    @Override
    @NotNull JooqxResultCollector resultCollector();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull SQLErrorConverter errorConverter();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull DataTypeMapperRegistry typeMapperRegistry();

    @SuppressWarnings("unchecked")
    @Override
    @NotNull JooqxTx transaction();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void batch(@NotNull Function<DSLContext, Query> queryFunction, @NotNull BindBatchValues bindBatchValues,
                       @NotNull Handler<AsyncResult<BatchResult>> handler) {
        JooqxBase.super.batch(queryFunction, bindBatchValues, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<BatchResult> batch(@NotNull Function<DSLContext, Query> queryFunction,
                                      @NotNull BindBatchValues bindBatchValues) {
        return JooqxBase.super.batch(queryFunction, bindBatchValues);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void execute(@NotNull Function<DSLContext, Query> queryFunction,
                                @NotNull SQLResultAdapter<T, R> resultAdapter,
                                @NotNull Handler<AsyncResult<@Nullable R>> handler) {
        JooqxBase.super.execute(queryFunction, resultAdapter, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> Future<@Nullable R> execute(@NotNull Function<DSLContext, Query> queryFunction,
                                               @NotNull SQLResultAdapter<T, R> resultAdapter) {
        return JooqxBase.super.execute(queryFunction, resultAdapter);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T, R> Future<R> execute(@NotNull Query query, @NotNull SQLResultAdapter<T, R> adapter);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void execute(@NotNull Query query, @NotNull SQLResultAdapter<T, R> adapter,
                                @NotNull Handler<AsyncResult<@Nullable R>> handler) {
        JooqxBase.super.execute(query, adapter, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void batch(@NotNull Query query, @NotNull BindBatchValues bindBatchValues,
                       @NotNull Handler<AsyncResult<BatchResult>> handler) {
        JooqxBase.super.batch(query, bindBatchValues, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<BatchResult> batch(@NotNull Query query, @NotNull BindBatchValues bindBatchValues);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T, R> Future<BatchReturningResult<R>> batch(@NotNull Query query, @NotNull BindBatchValues bindBatchValues,
                                                 @NotNull SQLResultAdapter.SQLResultListAdapter<T, R> adapter);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void batch(@NotNull Query query, @NotNull BindBatchValues bindBatchValues,
                              @NotNull SQLResultAdapter.SQLResultListAdapter<T, R> adapter,
                              @NotNull Handler<AsyncResult<BatchReturningResult<R>>> handler) {
        JooqxBase.super.batch(query, bindBatchValues, adapter, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void ddl(@NotNull Function<DSLContext, DDLQuery> ddlFunction,
                     @NotNull Handler<AsyncResult<Integer>> handler) {
        JooqxBase.super.ddl(ddlFunction, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<Integer> ddl(@NotNull Function<DSLContext, DDLQuery> ddlFunction) {
        return JooqxBase.super.ddl(ddlFunction);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void ddl(@NotNull DDLQuery query, @NotNull Handler<AsyncResult<Integer>> handler) {
        JooqxBase.super.ddl(query, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<Integer> ddl(@NotNull DDLQuery query);

}
