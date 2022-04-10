package io.zero88.jooqx.provider;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.jdbc.spi.DataSourceProvider;
import io.vertx.ext.sql.SQLClient;
import io.vertx.sqlclient.PoolOptions;

/**
 * A provider provides legacy SQL client that relies on an external JDBC connection pool.
 * <p>
 * Some well-known JDBC connection pool libs are: {@code c3p0}, {@code HikariCP}, and {@code Agroal}. These are official
 * support by {@code Vert.x}.
 *
 * @param <P> Type of DataSource provider
 * @see DataSourceProvider
 * @see SQLClient
 * @see JDBCClient
 * @since 2.0.0
 */
public interface LegacySQLClientProvider<P extends DataSourceProvider>
    extends SQLClientProvider<SQLClient>, SQLClientOptionParser<JsonObject>, JDBCExtension<P> {

    @Override
    default String sqlClientClass() {
        return "io.vertx.ext.jdbc.JDBCClient";
    }

    @Override
    default @NotNull Future<SQLClient> open(Vertx vertx, JsonObject connOptions, @Nullable JsonObject poolOptions) {
        return Future.succeededFuture(JDBCClient.create(vertx, parseConn(connOptions)));
    }

    @Override
    default @NotNull Future<Void> close(SQLClient sqlClient) {
        if (Objects.isNull(sqlClient)) {
            return Future.succeededFuture();
        }
        Promise<Void> promise = Promise.promise();
        sqlClient.close(promise);
        return promise.future();
    }

    @Override
    default @NotNull JsonObject parseConn(@NotNull JsonObject connOptions) {
        return optimizeDataSourceProviderConfig(connOptions);
    }

    @Override
    default @NotNull PoolOptions parsePool(@Nullable JsonObject poolOptions) {
        throw new UnsupportedOperationException(
            "Legacy Vertx SQL uses an external JDBC connection pool lib, then only using Connection Options");
    }

}
