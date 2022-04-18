package io.github.zero88.integtest.jooqx.h2;

import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.DDLTest;
import io.github.zero88.jooqx.JooqxTestDefinition.JooqxDBMemoryTest;
import io.github.zero88.jooqx.spi.h2.H2MemProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.VertxTestContext;

class H2ReADDLTest extends JooqxDBMemoryTest<JDBCPool>
    implements H2MemProvider, H2SQLHelper, JDBCPoolHikariProvider, DDLTest {

    @Test
    void test_create_table(VertxTestContext testContext) {
        createTableThenAssert(testContext, jooqx, schema().AUTHOR);
    }

}
