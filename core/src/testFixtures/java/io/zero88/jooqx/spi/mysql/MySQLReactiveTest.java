package io.zero88.jooqx.spi.mysql;

import org.testcontainers.containers.MySQLContainer;

import io.vertx.sqlclient.SqlClient;
import io.zero88.jooqx.DBContainerProvider;
import io.zero88.jooqx.ReactiveTestDefinition.ReactiveDBContainerTest;
import io.zero88.jooqx.SQLTest.ReactiveSQLTest;

public abstract class MySQLReactiveTest<S extends SqlClient> extends ReactiveDBContainerTest<S, MySQLContainer<?>>
    implements MySQLDBProvider, ReactiveSQLTest<S, MySQLContainer<?>, DBContainerProvider<MySQLContainer<?>>> {

}
