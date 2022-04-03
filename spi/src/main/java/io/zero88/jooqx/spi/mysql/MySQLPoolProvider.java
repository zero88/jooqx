package io.zero88.jooqx.spi.mysql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLPool;
import io.zero88.jooqx.provider.JooqxSQLClientProvider;

/**
 * MySQL pool provider
 *
 * @see MySQLPool
 * @since 1.1.0
 */
public interface MySQLPoolProvider extends JooqxSQLClientProvider<MySQLPool>, MySQLClientParser {

    @Override
    default String sqlClientClass() {
        return "io.vertx.mysqlclient.MySQLPool";
    }

    @Override
    default @NotNull Future<MySQLPool> open(Vertx vertx, JsonObject connOptions, @Nullable JsonObject poolOptions) {
        return Future.succeededFuture(MySQLPool.pool(vertx, parseConn(connOptions), parsePool(poolOptions)));
    }

}
