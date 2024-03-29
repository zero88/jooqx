package io.github.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

/**
 * Represents for a reactive SQL transaction executor
 *
 * @since 2.0.0
 */
@VertxGen
public interface JooqxTx
    extends JooqxConn, SQLTxExecutor<SqlConnection, Tuple, JooqxPreparedQuery, JooqxResultCollector, JooqxTx> {

    @Override
    default @NotNull JooqxTx transaction() { return this; }

    @Override
    default <X> void run(@NotNull Function<JooqxTx, Future<X>> transactionFn, @NotNull Handler<AsyncResult<X>> handler) {
        run(transactionFn).onComplete(handler);
    }

    @Override
    <X> Future<X> run(@NotNull Function<JooqxTx, Future<X>> transactionFn);

}
