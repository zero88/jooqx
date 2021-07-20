package io.zero88.jooqx.spi.mssql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mssqlclient.MSSQLPool;
import io.zero88.jooqx.provider.ReactiveSQLClientProvider;

/**
 * MSSQL pool provider
 *
 * @see MSSQLPool
 * @since 1.1.0
 */
public interface MSSQLPoolProvider extends ReactiveSQLClientProvider<MSSQLPool>, MSSQLClientParser {

    @Override
    default @NotNull Future<MSSQLPool> open(Vertx vertx, JsonObject connOptions, @Nullable JsonObject poolOptions) {
        return Future.succeededFuture(MSSQLPool.pool(vertx, parseConn(connOptions), parsePool(poolOptions)));
    }

}