package io.zero88.jooqx.spi.jdbc;

import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;
import io.zero88.jooqx.provider.JDBCExtension.HikariCPExtension;
import io.zero88.jooqx.provider.LegacySQLClientProvider;

/**
 * Legacy JDBC client provider for {@code HikariCP}
 *
 * @see HikariCPDataSourceProvider
 * @since 1.1.0
 */
public interface JDBCLegacyHikariProvider
    extends LegacySQLClientProvider<HikariCPDataSourceProvider>, HikariCPExtension {

}
