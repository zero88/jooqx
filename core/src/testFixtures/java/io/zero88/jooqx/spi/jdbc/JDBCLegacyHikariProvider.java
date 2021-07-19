package io.zero88.jooqx.spi.jdbc;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;
import io.zero88.jooqx.provider.LegacySQLClientProvider;

public interface JDBCLegacyHikariProvider extends LegacySQLClientProvider<HikariCPDataSourceProvider> {

    @Override
    default JsonObject optimizeConn(JsonObject connOption) {
        return LegacySQLClientProvider.super.optimizeConn(connOption.put("username", connOption.getString("user")));
    }

    @Override
    default Class<HikariCPDataSourceProvider> dataSourceProviderClass() {
        return HikariCPDataSourceProvider.class;
    }

}
