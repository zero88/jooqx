package io.zero88.jooqx.spi.pg;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnection;
import io.zero88.jooqx.provider.JooqxSQLClientProvider;

/**
 * PostgreSQL connection provider
 *
 * @see PgConnection
 * @since 1.1.0
 */
public interface PgConnProvider extends JooqxSQLClientProvider<PgConnection>, PgSQLClientParser {

    @Override
    default String sqlClientClass() {
        return "io.vertx.pgclient.PgConnection";
    }

    @Override
    default @NotNull Future<PgConnection> open(Vertx vertx, JsonObject connOptions, @Nullable JsonObject poolOptions) {
        return PgConnection.connect(vertx, parseConn(connOptions));
    }

}
