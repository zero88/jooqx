package io.zero88.jooqx;

import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.TableLike;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.zero88.jooqx.LegacySQLImpl.LegacyInternal;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.NonNull;

/**
 * Represents for an executor that use in legacy SQL transaction
 *
 * @since 1.0.0
 */
//@VertxGen
public interface LegacyJooqxTx extends LegacyInternal<SQLConnection>,
                                       SQLTxExecutor<SQLConnection, JsonArray, LegacySQLPreparedQuery, ResultSet,
                                                        LegacySQLCollector, LegacyJooqxTx> {

    @Override
    @NonNull Vertx vertx();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull DSLContext dsl();

    @Override
    @NonNull SQLConnection sqlClient();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull LegacySQLPreparedQuery preparedQuery();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull SQLErrorConverter errorConverter();

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    @NonNull DataTypeMapperRegistry typeMapperRegistry();

    @Override
    @SuppressWarnings("unchecked")
    default @NonNull LegacyJooqxTx transaction() {
        return this;
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T extends TableLike<?>, R> Future<@Nullable R> execute(@NonNull Query query,
                                                            @NonNull SQLResultAdapter<T, R> adapter);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T extends TableLike<?>, R> void execute(@NonNull Query query,
                                                     @NonNull SQLResultAdapter<T, R> resultAdapter,
                                                     @NonNull Handler<AsyncResult<@Nullable R>> handler) {
        LegacyInternal.super.execute(query, resultAdapter, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues,
                       @NonNull Handler<AsyncResult<BatchResult>> handler) {
        LegacyInternal.super.batch(query, bindBatchValues, handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<BatchResult> batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues);

    @Override
    default <X> void run(@NonNull Function<LegacyJooqxTx, Future<X>> function,
                         @NonNull Handler<AsyncResult<X>> handler) {
        SQLTxExecutor.super.run(function, handler);
    }

    @Override
    <X> Future<X> run(@NonNull Function<LegacyJooqxTx, Future<X>> function);

}
