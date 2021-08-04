package io.zero88.jooqx.spi.db2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.db2client.DB2Pool;
import io.zero88.jooqx.provider.JooqxSQLClientProvider;

/**
 * DB2 pool provider
 *
 * @see DB2Pool
 * @since 1.1.0
 */
public interface DB2PoolProvider extends JooqxSQLClientProvider<DB2Pool>, DB2ClientParser {

    @Override
    default String sqlClientClass() {
        return "io.vertx.db2client.DB2Pool";
    }

    @Override
    default @NotNull Future<DB2Pool> open(Vertx vertx, JsonObject connOptions, @Nullable JsonObject poolOptions) {
        return Future.succeededFuture(DB2Pool.pool(vertx, parseConn(connOptions), parsePool(poolOptions)));
    }

}
