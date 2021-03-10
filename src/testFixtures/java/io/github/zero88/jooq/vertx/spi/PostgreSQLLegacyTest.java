package io.github.zero88.jooq.vertx.spi;

import org.testcontainers.containers.PostgreSQLContainer;

import io.github.zero88.jooq.vertx.DBProvider.DBContainerProvider;
import io.github.zero88.jooq.vertx.LegacySQLTest;

public interface PostgreSQLLegacyTest
    extends PostgreSQLDBProvider, LegacySQLTest<PostgreSQLContainer<?>, DBContainerProvider<PostgreSQLContainer<?>>> {

}
