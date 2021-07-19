package io.zero88.jooqx.spi.pg;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.zero88.jooqx.provider.ReactiveSQLClientProvider;

import lombok.NonNull;

public interface PgPoolProvider extends ReactiveSQLClientProvider<PgPool>, PgSQLClientProvider {

    @Override
    default @NonNull Future<PgPool> open(Vertx vertx, JsonObject connOption) {
        return Future.succeededFuture(PgPool.pool(vertx, connectionOptions(connOption), poolOptions()));
    }

}
