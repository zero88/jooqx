package io.zero88.jooqx;

import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.TableLike;

import io.vertx.codegen.annotations.GenIgnore;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.zero88.jooqx.LegacySQLImpl.LegacyInternal;
import io.zero88.jooqx.adapter.SQLResultAdapter;

import lombok.NonNull;

/**
 * Represents for an executor that use in legacy SQL transaction
 *
 * @since 1.0.0
 */
@VertxGen
public interface LegacyJooqxTx extends LegacyInternal<SQLConnection>,
                                       SQLTxExecutor<SQLConnection, JsonArray, ResultSet, LegacySQLConverter,
                                                        LegacyJooqxTx> {

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
    @NonNull SQLErrorConverter<? extends Throwable, ? extends RuntimeException> errorConverter();

    @Override
    @SuppressWarnings("unchecked")
    default @NonNull LegacyJooqxTx transaction() {
        return this;
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default <T extends TableLike<?>, R> void execute(@NonNull Query query,
                                                     @NonNull SQLResultAdapter<ResultSet, LegacySQLConverter, T, R> resultAdapter,
                                                     @NonNull Handler<AsyncResult<@Nullable R>> handler) {
        execute(query, resultAdapter).onComplete(handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    <T extends TableLike<?>, R> Future<@Nullable R> execute(@NonNull Query query,
                                                            @NonNull SQLResultAdapter<ResultSet, LegacySQLConverter,
                                                                                         T, R> resultAdapter);

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    default void batch(Query query, @NonNull BindBatchValues bindBatchValues,
                       @NonNull Handler<AsyncResult<BatchResult>> handler) {
        batch(query, bindBatchValues).onComplete(handler);
    }

    @Override
    @GenIgnore(GenIgnore.PERMITTED_TYPE)
    Future<BatchResult> batch(@NonNull Query query, @NonNull BindBatchValues bindBatchValues);

    @Override
    <X> Future<X> run(@NonNull Function<LegacyJooqxTx, Future<X>> function);

    @Override
    default <X> void run(@NonNull Function<LegacyJooqxTx, Future<X>> function,
                         @NonNull Handler<AsyncResult<X>> handler) {
        run(function).onComplete(handler);
    }

}
