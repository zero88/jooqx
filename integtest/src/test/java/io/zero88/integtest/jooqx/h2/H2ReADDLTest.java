package io.zero88.integtest.jooqx.h2;

import org.junit.jupiter.api.Test;

import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.JooqxTestDefinition.JooqxDBMemoryTest;
import io.zero88.integtest.jooqx.DDLTest;
import io.zero88.jooqx.spi.h2.H2MemProvider;
import io.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;

public class H2ReADDLTest extends JooqxDBMemoryTest<JDBCPool>
    implements H2MemProvider, H2SQLHelper, JDBCPoolHikariProvider, DDLTest {

    @Test
    void test_create_table(VertxTestContext testContext) {
        createTableThenAssert(testContext, jooqx, schema().AUTHOR);
    }

}
