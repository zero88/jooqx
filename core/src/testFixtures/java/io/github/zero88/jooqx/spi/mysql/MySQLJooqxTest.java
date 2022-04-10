package io.github.zero88.jooqx.spi.mysql;

import org.testcontainers.containers.MySQLContainer;

import io.github.zero88.jooqx.DBContainerProvider;
import io.github.zero88.jooqx.JooqxTestDefinition.JooqxDBContainerTest;
import io.github.zero88.jooqx.SQLTest.JooqxTest;
import io.vertx.sqlclient.SqlClient;

public abstract class MySQLJooqxTest<S extends SqlClient> extends JooqxDBContainerTest<S, MySQLContainer<?>>
    implements MySQLDBProvider, JooqxTest<S, MySQLContainer<?>, DBContainerProvider<MySQLContainer<?>>> {

}
