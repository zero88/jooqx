package io.zero88.jooqx.spi.jdbc;

import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;
import io.zero88.jooqx.LegacyTestDefinition.LegacySQLClientProvider;

public interface JDBCLegacyHikariProvider extends LegacySQLClientProvider<HikariCPDataSourceProvider> {

    @Override
    default Class<HikariCPDataSourceProvider> dataSourceProviderClass() {
        return HikariCPDataSourceProvider.class;
    }

}
