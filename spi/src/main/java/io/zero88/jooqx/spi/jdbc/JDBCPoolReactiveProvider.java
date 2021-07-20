package io.zero88.jooqx.spi.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.zero88.jooqx.provider.ReactiveSQLClientProvider;
import io.zero88.jooqx.provider.SQLClientOptionParser;

import lombok.NonNull;

/**
 * Reactive JDBC pool provider
 *
 * @see JDBCPool
 * @since 1.1.0
 */
public interface JDBCPoolReactiveProvider
    extends ReactiveSQLClientProvider<JDBCPool>, SQLClientOptionParser<JDBCConnectOptions> {

    @Override
    default @NonNull Future<JDBCPool> open(Vertx vertx, JsonObject connOptions, @Nullable JsonObject poolOptions) {
        return Future.succeededFuture(JDBCPool.pool(vertx, parseConn(connOptions), parsePool(poolOptions)));
    }

    @Override
    @NotNull
    default JDBCConnectOptions parseConn(@NotNull JsonObject connOptions) {
        return new JDBCConnectOptions(connOptions);
    }

}
