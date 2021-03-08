package io.github.zero88.jooq.vertx.spi;

import org.testcontainers.containers.PostgreSQLContainer;

import io.github.zero88.jooq.vertx.BaseLegacySqlTest;
import io.github.zero88.jooq.vertx.DBProvider.DBContainerProvider;

public interface PostgreSQLJdbcTest extends PostgreSQLDBProvider,
                                            BaseLegacySqlTest<PostgreSQLContainer<?>,
                                                                 DBContainerProvider<PostgreSQLContainer<?>>> {

}
