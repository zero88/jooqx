package io.github.zero88.jooqx.spi.db2;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.jooqx.provider.SQLClientOptionParser;
import io.vertx.core.json.JsonObject;
import io.vertx.db2client.DB2ConnectOptions;

/**
 * DB2 connection option parser
 *
 * @see DB2ConnectOptions
 * @since 2.0.0
 */
public interface DB2ClientParser extends SQLClientOptionParser<DB2ConnectOptions> {

    @Override
    default @NotNull DB2ConnectOptions parseConn(@NotNull JsonObject connOptions) {
        return new DB2ConnectOptions(connOptions);
    }

}
