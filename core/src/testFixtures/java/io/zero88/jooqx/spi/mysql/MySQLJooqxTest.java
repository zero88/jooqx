package io.zero88.jooqx.spi.mysql;

import org.testcontainers.containers.MySQLContainer;

import io.vertx.sqlclient.SqlClient;
import io.zero88.jooqx.DBContainerProvider;
import io.zero88.jooqx.JooqxTestDefinition.JooqxDBContainerTest;
import io.zero88.jooqx.SQLTest.JooqxTest;

public abstract class MySQLJooqxTest<S extends SqlClient> extends JooqxDBContainerTest<S, MySQLContainer<?>>
    implements MySQLDBProvider, JooqxTest<S, MySQLContainer<?>, DBContainerProvider<MySQLContainer<?>>> {

}
