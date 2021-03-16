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
 * @since 1.0.0
 */
@VertxGen
public interface ReactiveJooqxTx extends ReactiveJooqxConn,
                                         SQLTxExecutor<SqlConnection, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>,
                                                          ReactiveSQLResultCollector, ReactiveJooqxTx> {

    @Override
    default @NonNull ReactiveJooqxTx transaction() {
        return this;
    }

    @Override
    default <X> void run(@NonNull Function<ReactiveJooqxTx, Future<X>> function,
                         @NonNull Handler<AsyncResult<X>> handler) {
        SQLTxExecutor.super.run(function, handler);
    }

    @Override
    <X> Future<X> run(@NonNull Function<ReactiveJooqxTx, Future<X>> function);

}
