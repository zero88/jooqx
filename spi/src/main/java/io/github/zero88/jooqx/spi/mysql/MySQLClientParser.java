package io.github.zero88.jooqx.spi.mysql;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.jooqx.provider.SQLClientOptionParser;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;

/**
 * MySQL connection option parser
 *
 * @see MySQLConnectOptions
 * @since 2.0.0
 */
public interface MySQLClientParser extends SQLClientOptionParser<MySQLConnectOptions> {

    @Override
    @NotNull
    default MySQLConnectOptions parseConn(@NotNull JsonObject connOptions) {
        return new MySQLConnectOptions(connOptions);
    }

}
