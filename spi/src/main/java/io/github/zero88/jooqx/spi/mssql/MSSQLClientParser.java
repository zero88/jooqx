package io.github.zero88.jooqx.spi.mssql;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.jooqx.provider.SQLClientOptionParser;
import io.vertx.core.json.JsonObject;
import io.vertx.mssqlclient.MSSQLConnectOptions;

/**
 * MSSQL connection option parser
 *
 * @see MSSQLConnectOptions
 * @since 2.0.0
 */
public interface MSSQLClientParser extends SQLClientOptionParser<MSSQLConnectOptions> {

    @Override
    default @NotNull MSSQLConnectOptions parseConn(@NotNull JsonObject connOptions) {
        return new MSSQLConnectOptions(connOptions);
    }

}
