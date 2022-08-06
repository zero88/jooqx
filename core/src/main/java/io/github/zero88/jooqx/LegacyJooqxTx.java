package io.github.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Query;

import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.SQLConnection;

/**
 * Represents for a legacy SQL transaction executor
 *
 * @since 1.0.0
 */
@VertxGen
@Deprecated
public interface LegacyJooqxTx extends LegacyInternal<SQLConnection>,
                                       SQLTxExecutor<SQLConnection, JsonArray, LegacySQLPreparedQuery,
                                                        LegacySQLCollector, LegacyJooqxTx> {

    @Override
    @NotNull Vertx vertx();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NotNull DSLContext dsl();

    @Override
    @NotNull SQLConnection sqlClient();

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
    default @NotNull LegacyJooqxTx transaction() {
        return this;
    }

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
    default <X> void run(@NotNull Function<LegacyJooqxTx, Future<X>> function,
                         @NotNull Handler<AsyncResult<X>> handler) {
        SQLTxExecutor.super.run(function, handler);
    }

    @Override
    <X> Future<X> run(@NotNull Function<LegacyJooqxTx, Future<X>> function);

}
