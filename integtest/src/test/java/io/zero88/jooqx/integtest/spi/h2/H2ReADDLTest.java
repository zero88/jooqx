package io.zero88.jooqx.integtest.spi.h2;

import org.junit.jupiter.api.Test;

import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.ReactiveTestDefinition.ReactiveDBMemoryTest;
import io.zero88.jooqx.integtest.spi.DDLTest;
import io.zero88.jooqx.spi.h2.H2MemProvider;
import io.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;

public class H2ReADDLTest extends ReactiveDBMemoryTest<JDBCPool>
    implements H2MemProvider, H2SQLHelper, JDBCPoolHikariProvider, DDLTest {

    @Test
    void test_create_table(VertxTestContext testContext) {
        createTableThenAssert(testContext, jooqx, schema().AUTHOR);
    }

}
