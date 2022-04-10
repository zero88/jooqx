package io.zero88.jooqx.spi.mssql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mssqlclient.MSSQLPool;
import io.zero88.jooqx.provider.JooqxSQLClientProvider;

/**
 * MSSQL pool provider
 *
 * @see MSSQLPool
 * @since 2.0.0
 */
public interface MSSQLPoolProvider extends JooqxSQLClientProvider<MSSQLPool>, MSSQLClientParser {

    @Override
    default String sqlClientClass() {
        return "io.vertx.mssqlclient.MSSQLPool";
    }

    @Override
    default @NotNull Future<MSSQLPool> open(Vertx vertx, JsonObject connOptions, @Nullable JsonObject poolOptions) {
        return Future.succeededFuture(MSSQLPool.pool(vertx, parseConn(connOptions), parsePool(poolOptions)));
    }

}
