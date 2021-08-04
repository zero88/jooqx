package io.zero88.jooqx.spi.pg;

import org.testcontainers.containers.PostgreSQLContainer;

import io.vertx.sqlclient.SqlClient;
import io.zero88.jooqx.DBContainerProvider;
import io.zero88.jooqx.JooqxTestDefinition.JooqxDBContainerTest;
import io.zero88.jooqx.SQLTest.JooqxTest;

public abstract class PgSQLJooqxTest<S extends SqlClient> extends JooqxDBContainerTest<S, PostgreSQLContainer<?>>
    implements PgSQLDBProvider, JooqxTest<S, PostgreSQLContainer<?>, DBContainerProvider<PostgreSQLContainer<?>>> {

}
