package io.zero88.jooqx.spi.mysql;

import java.nio.charset.StandardCharsets;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.zero88.jooqx.provider.SQLClientOptionParser;

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
        return new MySQLConnectOptions(connOptions).setCharset("utf8")
                                                   .setCharacterEncoding(StandardCharsets.UTF_8.name())
                                                   .setCollation("utf8_general_ci");
    }

}
