package io.github.zero88.jooqx.spi.pg;

import org.jooq.Record;
import org.junit.jupiter.api.Assertions;
import org.testcontainers.containers.PostgreSQLContainer;

import io.github.zero88.jooqx.DBContainerProvider;
import io.github.zero88.jooqx.JooqxTestDefinition.JooqxDBContainerTest;
import io.github.zero88.jooqx.SQLTest.JooqxTest;
import io.github.zero88.jooqx.SQLTestHelper;
import io.vertx.sqlclient.SqlClient;

public abstract class PgSQLJooqxTest<S extends SqlClient> extends JooqxDBContainerTest<S, PostgreSQLContainer<?>>
    implements PgSQLDBProvider, JooqxTest<S, PostgreSQLContainer<?>, DBContainerProvider<PostgreSQLContainer<?>>> {

    public static void assertPostgresVersion(Record rec) {
        final Object value = rec.getValue(0);
        final String dbVersion = SQLTestHelper.getCurrentDBVersion("16-alpine").substring(0, 2);
        Assertions.assertInstanceOf(String.class, value);
        Assertions.assertTrue(((String) value).contains("PostgreSQL " + dbVersion), "Version is mot match");
    }

}
