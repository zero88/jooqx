package io.zero88.jooqx.spi.pg;

import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.zero88.jooqx.provider.ReactiveSQLClientProvider;

import lombok.NonNull;

/**
 * PostgreSQL pool provider
 *
 * @see PgPool
 * @since 1.1.0
 */
public interface PgPoolProvider extends ReactiveSQLClientProvider<PgPool>, PgSQLClientParser {

    @Override
    default @NonNull Future<PgPool> open(Vertx vertx, JsonObject connOptions, @Nullable JsonObject poolOptions) {
        return Future.succeededFuture(PgPool.pool(vertx, parseConn(connOptions), parsePool(poolOptions)));
    }

}
