package io.zero88.jooqx.spi.jdbc;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.zero88.jooqx.provider.JDBCExtension.HikariCPExtension;

/**
 * Provides a reactive JDBC Pool that using {@code HikariCP}
 *
 * @see JDBCPool
 * @see HikariCPExtension
 * @since 2.0.0
 */
public interface JDBCPoolHikariProvider extends JDBCPoolProvider<HikariCPDataSourceProvider>, HikariCPExtension {

    @Override
    default @NotNull JDBCConnectOptions parseConn(@NotNull JsonObject connOptions) {
        return JDBCPoolProvider.super.parseConn(connOptions.put("dataSourceImplementation", "hikari"));
    }

}
