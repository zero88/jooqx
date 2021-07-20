package io.zero88.jooqx.spi.jdbc;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;
import io.zero88.jooqx.provider.LegacySQLClientProvider;

/**
 * Legacy JDBC client provider for {@code HikariCP}
 *
 * @see HikariCPDataSourceProvider
 * @since 1.1.0
 */
public interface JDBCLegacyHikariProvider extends LegacySQLClientProvider<HikariCPDataSourceProvider> {

    @Override
    @NotNull
    default JsonObject parseConn(@NotNull JsonObject connOptions) {
        return LegacySQLClientProvider.super.parseConn(connOptions.put("username", connOptions.getString("user")));
    }

    @Override
    default Class<HikariCPDataSourceProvider> dataSourceProviderClass() {
        return HikariCPDataSourceProvider.class;
    }

}
