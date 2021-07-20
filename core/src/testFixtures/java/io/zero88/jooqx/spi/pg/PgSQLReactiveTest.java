package io.zero88.jooqx.spi.pg;

import org.testcontainers.containers.PostgreSQLContainer;

import io.vertx.sqlclient.SqlClient;
import io.zero88.jooqx.DBContainerProvider;
import io.zero88.jooqx.ReactiveTestDefinition.ReactiveDBContainerTest;
import io.zero88.jooqx.ReactiveTestDefinition.ReactiveSQLTest;

public abstract class PgSQLReactiveTest<S extends SqlClient> extends ReactiveDBContainerTest<S, PostgreSQLContainer<?>>
    implements PgSQLDBProvider,
               ReactiveSQLTest<S, PostgreSQLContainer<?>, DBContainerProvider<PostgreSQLContainer<?>>> {

}
