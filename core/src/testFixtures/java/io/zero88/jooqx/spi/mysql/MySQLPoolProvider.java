package io.zero88.jooqx.spi.mysql;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.zero88.jooqx.provider.ReactiveSQLClientProvider;

import lombok.NonNull;

public interface MySQLPoolProvider extends ReactiveSQLClientProvider<MySQLPool>, MySQLClientProvider {

    @Override
    default @NonNull Future<MySQLPool> open(Vertx vertx, JsonObject connOption) {
        return Future.succeededFuture(MySQLPool.pool(vertx, connectionOptions(connOption), poolOptions()));
    }

}
