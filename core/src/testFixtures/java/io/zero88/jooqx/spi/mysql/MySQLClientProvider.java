package io.zero88.jooqx.spi.mysql;

import java.nio.charset.StandardCharsets;

import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnectOptions;

interface MySQLClientProvider {

    default MySQLConnectOptions connectionOptions(JsonObject server) {

        return new MySQLConnectOptions(server).setCharset("utf8")
                                              .setCharacterEncoding(StandardCharsets.UTF_8.name())
                                              .setCollation("utf8_general_ci");
    }

}
