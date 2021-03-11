package io.zero88.jooqx.spi;

import org.testcontainers.containers.PostgreSQLContainer;

import io.zero88.jooqx.DBProvider.DBContainerProvider;
import io.zero88.jooqx.LegacySQLTest;

public interface PostgreSQLLegacyTest
    extends PostgreSQLDBProvider, LegacySQLTest<PostgreSQLContainer<?>, DBContainerProvider<PostgreSQLContainer<?>>> {

}
