package io.github.zero88.jooqx.spi.db2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.zero88.jooqx.provider.JooqxSQLClientProvider;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.db2client.DB2Pool;

/**
 * DB2 pool provider
 *
 * @see DB2Pool
 * @since 2.0.0
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
