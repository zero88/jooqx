package io.github.zero88.jooqx.spi.pg;

import org.testcontainers.containers.PostgreSQLContainer;

import io.github.zero88.jooqx.DBContainerProvider;
import io.github.zero88.jooqx.LegacyTestDefinition.LegacyDBContainerTest;
import io.github.zero88.jooqx.SQLTest.LegacySQLTest;
import io.github.zero88.jooqx.spi.jdbc.JDBCLegacyHikariProvider;
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;

public abstract class PgSQLLegacyTest extends LegacyDBContainerTest<PostgreSQLContainer<?>, HikariCPDataSourceProvider>
    implements PgSQLDBProvider, JDBCLegacyHikariProvider, LegacySQLTest<PostgreSQLContainer<?>, DBContainerProvider<PostgreSQLContainer<?>>,
                                    HikariCPDataSourceProvider> {

}
