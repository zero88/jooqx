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
 * @since 2.0.0
 */
@VertxGen
@Deprecated
public interface LegacyJooqxSession extends LegacyInternal<SQLConnection>,
                                            SQLSessionExecutor<SQLConnection, JsonArray, LegacySQLPreparedQuery,
                                                                  LegacySQLCollector, LegacyJooqxSession> {

    @Override
    default @NotNull LegacyJooqxSession session() { return this; }

    @Override
    @NotNull LegacyJooqxTx transaction();

    @Override
    default <R> void perform(@NotNull Function<LegacyJooqxSession, Future<R>> sessionFn,
                             @NotNull Handler<AsyncResult<R>> handler) {
        perform(sessionFn).onComplete(handler);
    }

    @Override
    <R> Future<R> perform(@NotNull Function<LegacyJooqxSession, Future<R>> sessionFn);

}
