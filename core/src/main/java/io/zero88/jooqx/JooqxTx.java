package io.zero88.jooqx;

import java.util.function.Function;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;

import lombok.NonNull;

/**
 * Represents for a reactive SQL transaction executor
 *
 * @since 1.1.0
 */
@VertxGen
public interface JooqxTx extends JooqxConn,
                                 SQLTxExecutor<SqlConnection, Tuple, JooqxPreparedQuery, RowSet<Row>, JooqxResultCollector, JooqxTx> {

    @Override
    default @NonNull JooqxTx transaction() {
        return this;
    }

    @Override
    default <X> void run(@NonNull Function<JooqxTx, Future<X>> function, @NonNull Handler<AsyncResult<X>> handler) {
        SQLTxExecutor.super.run(function, handler);
    }

    @Override
    <X> Future<X> run(@NonNull Function<JooqxTx, Future<X>> function);

}
