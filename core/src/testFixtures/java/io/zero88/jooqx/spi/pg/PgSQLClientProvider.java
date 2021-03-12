package io.zero88.jooqx.spi.pg;

import io.vertx.pgclient.PgConnectOptions;
import io.zero88.jooqx.SQLConnectionOption;

interface PgSQLClientProvider {

    default PgConnectOptions connectionOptions(SQLConnectionOption server) {
        return new PgConnectOptions().setHost(server.getHost())
                                     .setPort(server.getPort())
                                     .setDatabase(server.getDatabase())
                                     .setUser(server.getUsername())
                                     .setPassword(server.getPassword());
    }

}
