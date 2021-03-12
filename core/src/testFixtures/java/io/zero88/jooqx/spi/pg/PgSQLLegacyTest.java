package io.zero88.jooqx.spi.pg;

import org.testcontainers.containers.PostgreSQLContainer;

import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;
import io.zero88.jooqx.DBProvider.DBContainerProvider;
import io.zero88.jooqx.LegacyTestDefinition.LegacyDBContainerTest;
import io.zero88.jooqx.LegacyTestDefinition.LegacySQLTest;
import io.zero88.jooqx.spi.jdbc.JDBCLegacyHikariProvider;

public abstract class PgSQLLegacyTest extends LegacyDBContainerTest<PostgreSQLContainer<?>, HikariCPDataSourceProvider>
    implements PgSQLDBProvider, JDBCLegacyHikariProvider,
               LegacySQLTest<PostgreSQLContainer<?>, DBContainerProvider<PostgreSQLContainer<?>>,
                                HikariCPDataSourceProvider> {

}
