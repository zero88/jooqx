package io.zero88.jooqx.spi.pg;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.zero88.jooqx.provider.JooqxSQLClientProvider;

/**
 * PostgreSQL pool provider
 *
 * @see PgPool
 * @since 2.0.0
 */
public interface PgPoolProvider extends JooqxSQLClientProvider<PgPool>, PgSQLClientParser {

    @Override
    default String sqlClientClass() {
        return "io.vertx.pgclient.PgPool";
    }

    @Override
    default @NotNull Future<PgPool> open(Vertx vertx, JsonObject connOptions, @Nullable JsonObject poolOptions) {
        return Future.succeededFuture(PgPool.pool(vertx, parseConn(connOptions), parsePool(poolOptions)));
    }

}
