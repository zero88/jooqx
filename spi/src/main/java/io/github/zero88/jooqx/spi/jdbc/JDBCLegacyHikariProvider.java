package io.github.zero88.jooqx.spi.jdbc;

import io.github.zero88.jooqx.provider.JDBCExtension.HikariCPExtension;
import io.github.zero88.jooqx.provider.LegacySQLClientProvider;
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;

/**
 * Legacy JDBC client provider for {@code HikariCP}
 *
 * @see HikariCPDataSourceProvider
 * @since 2.0.0
 */
public interface JDBCLegacyHikariProvider
    extends LegacySQLClientProvider<HikariCPDataSourceProvider>, HikariCPExtension {

}
