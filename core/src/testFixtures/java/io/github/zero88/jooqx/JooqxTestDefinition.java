package io.github.zero88.jooqx;

import org.testcontainers.containers.JdbcDatabaseContainer;

import io.github.zero88.jooqx.SQLTest.JooqxTest;
import io.github.zero88.jooqx.SQLTestImpl.DBContainerSQLTest;
import io.github.zero88.jooqx.SQLTestImpl.DBMemorySQLTest;
import io.github.zero88.jooqx.provider.DBEmbeddedProvider.DBMemoryProvider;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

public interface JooqxTestDefinition {

    //@formatter:off
    abstract class JooqxDBContainerTest<S extends SqlClient, K extends JdbcDatabaseContainer<?>>
        extends DBContainerSQLTest<S, Tuple, JooqxPreparedQuery,  JooqxResultCollector, JooqxBase<S>, K>
        implements JooqxTest<S, K, DBContainerProvider<K>> {

    }

    abstract class JooqxDBMemoryTest<S extends SqlClient>
        extends DBMemorySQLTest<S, Tuple, JooqxPreparedQuery,  JooqxResultCollector, JooqxBase<S>>
        implements JooqxTest<S, String, DBMemoryProvider> {

    }

}
