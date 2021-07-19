package io.zero88.jooqx.spi.jdbc;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.zero88.jooqx.provider.ReactiveSQLClientProvider;

import lombok.NonNull;

public interface JDBCReactiveProvider extends ReactiveSQLClientProvider<JDBCPool> {

    @Override
    default @NonNull Future<JDBCPool> open(Vertx vertx, JsonObject connOption) {
        return Future.succeededFuture(JDBCPool.pool(vertx, new JDBCConnectOptions(connOption), poolOptions()));
    }

}
