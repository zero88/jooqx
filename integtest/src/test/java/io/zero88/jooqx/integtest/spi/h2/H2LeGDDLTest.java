package io.zero88.jooqx.integtest.spi.h2;

import org.junit.jupiter.api.Test;

import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.LegacyTestDefinition.LegacyDBMemoryTest;
import io.zero88.jooqx.integtest.spi.DDLTest;
import io.zero88.jooqx.spi.h2.H2MemProvider;
import io.zero88.jooqx.spi.jdbc.JDBCLegacyHikariProvider;

public class H2LeGDDLTest extends LegacyDBMemoryTest<HikariCPDataSourceProvider>
    implements H2MemProvider, H2SQLHelper, JDBCLegacyHikariProvider, DDLTest {

    @Test
    void test_create_table(VertxTestContext testContext) {
        createTableThenAssert(testContext, jooqx, schema().AUTHOR);
    }

}
