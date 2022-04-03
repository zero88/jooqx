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
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.SQLOperations;
import io.zero88.jooqx.LegacySQLImpl.LegacyInternal;
import io.zero88.jooqx.LegacySQLImpl.LegacyJooqxImpl;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx legacy JDBC client} connection
 *
 * @see SQLClient
 * @see SQLConnection
 * @see SQLOperations
 * @see JDBCClient
 * @see ResultSet
 * @since 1.0.0
 */
@VertxGen
public interface LegacyJooqx extends LegacySQLImpl.LegacyInternal<SQLClient> {

    @GenIgnore
    static LegacyJooqxBuilder builder() {
        return new LegacyJooqxBuilder();
    }

    @Override
    @NotNull Vertx vertx();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull DSLContext dsl();

    @Override
    @NotNull SQLClient sqlClient();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull LegacySQLPreparedQuery preparedQuery();

    @Override
    @NotNull LegacySQLCollector resultCollector();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull SQLErrorConverter errorConverter();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull DataTypeMapperRegistry typeMapperRegistry();

    @Override
    @SuppressWarnings("unchecked")
    @NotNull LegacyJooqxTx transaction();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T, R> Future<@Nullable R> execute(@NotNull Query query, @NotNull SQLResultAdapter<T, R> adapter);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void execute(@NotNull Query query, @NotNull SQLResultAdapter<T, R> resultAdapter,
                                @NotNull Handler<AsyncResult<@Nullable R>> handler) {
        LegacyInternal.super.execute(query, resultAdapter, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void batch(@NotNull Query query, @NotNull BindBatchValues bindBatchValues,
                       @NotNull Handler<AsyncResult<BatchResult>> handler) {
        LegacyInternal.super.batch(query, bindBatchValues, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<BatchResult> batch(@NotNull Query query, @NotNull BindBatchValues bindBatchValues);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void batch(@NotNull Function<DSLContext, Query> queryFunction, @NotNull BindBatchValues bindBatchValues,
                       @NotNull Handler<AsyncResult<BatchResult>> handler) {
        LegacyInternal.super.batch(queryFunction, bindBatchValues, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<BatchResult> batch(@NotNull Function<DSLContext, Query> queryFunction,
                                      @NotNull BindBatchValues bindBatchValues) {
        return LegacyInternal.super.batch(queryFunction, bindBatchValues);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> void execute(@NotNull Function<DSLContext, Query> queryFunction,
                                @NotNull SQLResultAdapter<T, R> resultAdapter,
                                @NotNull Handler<AsyncResult<@Nullable R>> handler) {
        LegacyInternal.super.execute(queryFunction, resultAdapter, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T, R> Future<@Nullable R> execute(@NotNull Function<DSLContext, Query> queryFunction,
                                               @NotNull SQLResultAdapter<T, R> resultAdapter) {
        return LegacyInternal.super.execute(queryFunction, resultAdapter);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void ddl(@NotNull Function<DSLContext, DDLQuery> ddlFunction,
                     @NotNull Handler<AsyncResult<Integer>> handler) {
        LegacyInternal.super.ddl(ddlFunction, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default Future<Integer> ddl(@NotNull Function<DSLContext, DDLQuery> ddlFunction) {
        return LegacyInternal.super.ddl(ddlFunction);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void ddl(@NotNull DDLQuery query, @NotNull Handler<AsyncResult<Integer>> handler) {
        LegacyInternal.super.ddl(query, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<Integer> ddl(@NotNull DDLQuery query);

    @GenIgnore
    class LegacyJooqxBuilder extends SQLExecutorBuilder<SQLClient, JsonArray, LegacySQLPreparedQuery, ResultSet,
                                                       LegacySQLCollector, LegacyJooqx> {

        @Override
        public LegacyJooqx build() {
            return new LegacyJooqxImpl(vertx(), dsl(), sqlClient(), preparedQuery(), resultCollector(),
                                       errorConverter(), typeMapperRegistry());
        }

    }

}
