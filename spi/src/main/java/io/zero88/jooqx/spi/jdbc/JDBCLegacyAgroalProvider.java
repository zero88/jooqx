package io.zero88.jooqx.spi.jdbc;

import io.vertx.ext.jdbc.spi.impl.AgroalCPDataSourceProvider;
import io.zero88.jooqx.provider.JDBCExtension.AgroalExtension;
import io.zero88.jooqx.provider.LegacySQLClientProvider;

/**
 * Legacy JDBC client provider for {@code Agroal DataSource}
 *
 * @see AgroalCPDataSourceProvider
 * @since 2.0.0
 */
public interface JDBCLegacyAgroalProvider extends LegacySQLClientProvider<AgroalCPDataSourceProvider>, AgroalExtension {

}
