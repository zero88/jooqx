package io.github.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
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
    @NotNull LegacyJooqxSession session();

    @Override
    default @NotNull LegacyJooqxTx transaction() { return this; }

    @Override
    default <X> void run(@NotNull Function<LegacyJooqxTx, Future<X>> transactionFn,
                         @NotNull Handler<AsyncResult<X>> handler) {
        run(transactionFn).onComplete(handler);
    }

    @Override
    <X> Future<X> run(@NotNull Function<LegacyJooqxTx, Future<X>> transactionFn);

}
