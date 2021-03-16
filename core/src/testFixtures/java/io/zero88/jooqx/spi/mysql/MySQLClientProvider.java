package io.zero88.jooqx.spi.mysql;

import io.vertx.mysqlclient.MySQLConnectOptions;
import io.zero88.jooqx.SQLConnectionOption;

interface MySQLClientProvider {

    default MySQLConnectOptions connectionOptions(SQLConnectionOption server) {
        return new MySQLConnectOptions().setHost(server.getHost())
                                        .setPort(server.getPort())
                                        .setDatabase(server.getDatabase())
                                        .setUser(server.getUsername())
                                        .setPassword(server.getPassword());
    }

}
