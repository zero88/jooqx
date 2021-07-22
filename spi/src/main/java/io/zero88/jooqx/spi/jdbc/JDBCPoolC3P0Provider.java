package io.zero88.jooqx.spi.jdbc;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.spi.impl.C3P0DataSourceProvider;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
import io.zero88.jooqx.provider.JDBCExtension.C3P0Extension;

/**
 * Provides a reactive JDBC pool that using {@code C3P0}
 *
 * @see JDBCPool#pool(Vertx, JDBCConnectOptions, PoolOptions)
 * @see JDBCPool
 * @since 1.1.0
 */
public interface JDBCPoolC3P0Provider extends InternalJDBCPoolProvider<C3P0DataSourceProvider>, C3P0Extension {

    @Override
    @NotNull
    default JDBCConnectOptions parseConn(@NotNull JsonObject connOptions) {
        return InternalJDBCPoolProvider.super.parseConn(connOptions.put("dataSourceImplementation", "c3p0"));
    }

}
