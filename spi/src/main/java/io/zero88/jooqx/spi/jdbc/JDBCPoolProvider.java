package io.zero88.jooqx.spi.jdbc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.spi.DataSourceProvider;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.zero88.jooqx.provider.JDBCExtension;
import io.zero88.jooqx.provider.JooqxSQLClientProvider;
import io.zero88.jooqx.provider.SQLClientOptionParser;

/**
 * Reactive JDBC pool provider
 *
 * @see JDBCPool
 * @since 2.0.0
 */
public interface JDBCPoolProvider<P extends DataSourceProvider>
    extends JooqxSQLClientProvider<JDBCPool>, SQLClientOptionParser<JDBCConnectOptions>, JDBCExtension<P> {

    @Override
    default String sqlClientClass() {
        return "io.vertx.jdbcclient.JDBCPool";
    }

    @Override
    default @NotNull Future<JDBCPool> open(Vertx vertx, JsonObject connOptions, @Nullable JsonObject poolOptions) {
        final JDBCConnectOptions jdbcConnectOptions = parseConn(connOptions);
        if ("AGROAL".equals(jdbcConnectOptions.getDataSourceImplementation())) {
            return Future.succeededFuture(JDBCPool.pool(vertx, jdbcConnectOptions, parsePool(poolOptions)));
        }
        return Future.succeededFuture(JDBCPool.pool(vertx, optimizeDataSourceProviderConfig(connOptions)));
    }

    @Override
    @NotNull
    default JDBCConnectOptions parseConn(@NotNull JsonObject connOptions) {
        return new JDBCConnectOptions(connOptions);
    }

}
