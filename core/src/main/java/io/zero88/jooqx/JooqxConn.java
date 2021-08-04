package io.zero88.jooqx;

import java.util.function.Function;

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
import io.vertx.sqlclient.SqlConnection;
import io.zero88.jooqx.JooqxSQLImpl.JooqxConnImpl;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.NonNull;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx reactive SQL client} connection
 *
 * @see SqlConnection
 * @since 1.1.0
 */
@VertxGen
public interface JooqxConn extends JooqxBase<SqlConnection> {

    @GenIgnore
    static JooqxConnBuilder builder() {
        return new JooqxConnBuilder();
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
    @NonNull JooqxPreparedQuery preparedQuery();

    @Override
    @NonNull JooqxResultCollector resultCollector();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void batch(@NonNull Function<DSLContext, Query> queryFunction, @NonNull BindBatchValues bindBatchValues,
                       @NonNull Handler<AsyncResult<BatchResult>> handler) {
        JooqxBase.super.batch(queryFunction, bindBatchValues, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<BatchResult> batch(@NonNull Function<DSLContext, Query> queryFunction,
                                      @NonNull BindBatchValues bindBatchValues) {
        return JooqxBase.super.batch(queryFunction, bindBatchValues);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void execute(@NonNull Function<DSLContext, Query> queryFunction,
                                @NonNull SQLResultAdapter<T, R> resultAdapter,
                                @NonNull Handler<AsyncResult<@Nullable R>> handler) {
        JooqxBase.super.execute(queryFunction, resultAdapter, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> Future<@Nullable R> execute(@NonNull Function<DSLContext, Query> queryFunction,
                                               @NonNull SQLResultAdapter<T, R> resultAdapter) {
        return JooqxBase.super.execute(queryFunction, resultAdapter);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull SQLErrorConverter errorConverter();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull DataTypeMapperRegistry typeMapperRegistry();

    @Override
    @SuppressWarnings("unchecked")
    @NonNull JooqxTx transaction();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T, R> Future<R> execute(@NonNull Query query, @NonNull SQLResultAdapter<T, R> adapter);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void execute(@NonNull Query query, @NonNull SQLResultAdapter<T, R> adapter,
                                @NonNull Handler<AsyncResult<@Nullable R>> handler) {
        JooqxBase.super.execute(query, adapter, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
                       @NonNull Handler<AsyncResult<BatchResult>> handler) {
        JooqxBase.super.batch(query, bindBatchValues, handler);
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
        JooqxBase.super.batch(query, bindBatchValues, adapter, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void ddl(@NonNull Function<DSLContext, DDLQuery> ddlFunction,
                     @NonNull Handler<AsyncResult<Integer>> handler) {
        JooqxBase.super.ddl(ddlFunction, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<Integer> ddl(@NonNull Function<DSLContext, DDLQuery> ddlFunction) {
        return JooqxBase.super.ddl(ddlFunction);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void ddl(@NonNull DDLQuery query, @NonNull Handler<AsyncResult<Integer>> handler) {
        JooqxBase.super.ddl(query, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<Integer> ddl(@NonNull DDLQuery query);

    @GenIgnore
    class JooqxConnBuilder extends JooqxBaseBuilder<SqlConnection, JooqxConn> {

        @Override
        public JooqxConn build() {
            return new JooqxConnImpl(vertx(), dsl(), sqlClient(), preparedQuery(), resultCollector(),
                                     errorConverter(), typeMapperRegistry());
        }

    }

}
