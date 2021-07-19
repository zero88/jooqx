package io.zero88.jooqx.provider;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.jdbc.spi.DataSourceProvider;
import io.vertx.ext.sql.SQLClient;

import lombok.NonNull;

public interface LegacySQLClientProvider<P extends DataSourceProvider> extends SQLClientProvider<SQLClient> {

    @Override
    default @NonNull Future<SQLClient> open(Vertx vertx, JsonObject connOption) {
        return Future.succeededFuture(JDBCClient.create(vertx, optimizeConn(connOption)));
    }

    @Override
    default Future<Void> close() {
        Promise<Void> promise = Promise.promise();
        sqlClient().close(promise);
        return promise.future();
    }

    Class<P> dataSourceProviderClass();

    default JsonObject optimizeConn(JsonObject connOption) {
        return connOption.put("provider_class", dataSourceProviderClass().getName());
    }

}
