package io.zero88.jooqx.spi.jdbc;

import io.vertx.ext.jdbc.spi.impl.C3P0DataSourceProvider;
import io.zero88.jooqx.provider.LegacySQLClientProvider;

/**
 * Legacy JDBC client provider for {@code C3P0 DataSource}
 *
 * @see C3P0DataSourceProvider
 * @since 1.1.0
 */
public interface JDBCLegacyC3P0Provider extends LegacySQLClientProvider<C3P0DataSourceProvider> {

    @Override
    default Class<C3P0DataSourceProvider> dataSourceProviderClass() {
        return C3P0DataSourceProvider.class;
    }

}
