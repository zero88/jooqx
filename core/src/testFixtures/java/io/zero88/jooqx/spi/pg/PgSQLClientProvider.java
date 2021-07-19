package io.zero88.jooqx.spi.pg;

import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgConnectOptions;

interface PgSQLClientProvider {

    default PgConnectOptions connectionOptions(JsonObject server) {
        return new PgConnectOptions(server);
    }

}
