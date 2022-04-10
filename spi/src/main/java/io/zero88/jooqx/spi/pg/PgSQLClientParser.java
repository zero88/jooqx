package io.zero88.jooqx.spi.pg;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;
import io.zero88.jooqx.provider.SQLClientOptionParser;

/**
 * PostgreSQL connection option parser
 *
 * @see PgConnectOptions
 * @since 2.0.0
 */
public interface PgSQLClientParser extends SQLClientOptionParser<PgConnectOptions> {

    @Override
    default @NotNull PgConnectOptions parseConn(@NotNull JsonObject connOptions) {
        return new PgConnectOptions(connOptions);
    }

}
