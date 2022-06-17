package io.github.zero88.jooqx.spi.mysql;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.zero88.jooqx.provider.JooqxSQLClientProvider;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.mysqlclient.MySQLConnection;

public interface MySQLConnProvider extends JooqxSQLClientProvider<MySQLConnection>, MySQLClientParser {

    @Override
    default String sqlClientClass() {
        return "io.vertx.mysqlclient.MySQLConnection";
    }

    @Override
    default @NotNull Future<MySQLConnection> open(Vertx vertx, JsonObject connOptions,
                                                  @Nullable JsonObject poolOptions) {
        return MySQLConnection.connect(vertx, parseConn(connOptions));
    }

}
