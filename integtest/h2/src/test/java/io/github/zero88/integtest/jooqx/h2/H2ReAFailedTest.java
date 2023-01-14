package io.github.zero88.integtest.jooqx.h2;

import java.util.Arrays;

import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.JooqxTestDefinition.JooqxDBMemoryTest;
import io.github.zero88.jooqx.spi.h2.H2MemProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.github.zero88.sample.model.h2.tables.Author;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.VertxTestContext;

class H2ReAFailedTest extends JooqxDBMemoryTest<JDBCPool>
    implements H2MemProvider, H2SQLHelper, JDBCPoolHikariProvider {

    @Test
    void test(VertxTestContext testContext) {
        final Author table = schema().AUTHOR;
        jooqx.insert(dsl -> dsl.insertInto(table, table.ID, table.FIRST_NAME)
                               .values(Arrays.asList(DSL.defaultValue(table.ID), "abc")), ar -> {
            final String errorMsg = "Table \"AUTHOR\" not found; SQL statement:\n" + "insert into \"AUTHOR\" " +
                                    "(\"ID\", \"FIRST_NAME\") values (default, ?) [42102-200]";
            assertJooqException(testContext, ar, SQLStateClass.C42_SYNTAX_ERROR_OR_ACCESS_RULE_VIOLATION, errorMsg);
        });
    }

}
