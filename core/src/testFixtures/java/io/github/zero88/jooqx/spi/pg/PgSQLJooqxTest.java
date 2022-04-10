package io.github.zero88.jooqx.spi.pg;

import org.testcontainers.containers.PostgreSQLContainer;

import io.github.zero88.jooqx.DBContainerProvider;
import io.github.zero88.jooqx.JooqxTestDefinition.JooqxDBContainerTest;
import io.github.zero88.jooqx.SQLTest.JooqxTest;
import io.vertx.sqlclient.SqlClient;

public abstract class PgSQLJooqxTest<S extends SqlClient> extends JooqxDBContainerTest<S, PostgreSQLContainer<?>>
    implements PgSQLDBProvider, JooqxTest<S, PostgreSQLContainer<?>, DBContainerProvider<PostgreSQLContainer<?>>> {

}
