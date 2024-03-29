package io.github.zero88.jooqx.spi.jdbc;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.jooqx.provider.JDBCExtension.C3P0Extension;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.spi.impl.C3P0DataSourceProvider;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;

/**
 * Provides a reactive JDBC pool that using {@code C3P0}
 *
 * @see JDBCPool#pool(Vertx, JDBCConnectOptions, PoolOptions)
 * @see JDBCPool
 * @since 2.0.0
 */
public interface JDBCPoolC3P0Provider extends JDBCPoolProvider<C3P0DataSourceProvider>, C3P0Extension {

    @Override
    @NotNull
    default JDBCConnectOptions parseConn(@NotNull JsonObject connOptions) {
        return JDBCPoolProvider.super.parseConn(connOptions.put("dataSourceImplementation", "c3p0"));
    }

}
