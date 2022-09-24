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
 * Represents for a reactive SQL session executor
 *
 * @since 2.0.0
 */
@VertxGen
public interface JooqxSession extends JooqxConn,
                                      SQLSessionExecutor<SqlConnection, Tuple, JooqxPreparedQuery,
                                                            JooqxResultCollector, JooqxSession> {

    @Override
    default @NotNull JooqxSession session() { return this; }

    default <R> void perform(@NotNull Function<JooqxSession, Future<R>> sessionFn,
                             @NotNull Handler<AsyncResult<R>> handler) {
        perform(sessionFn).onComplete(handler);
    }

    @Override
    <R> Future<R> perform(@NotNull Function<JooqxSession, Future<R>> sessionFn);

}
