package io.github.zero88.integtest.jooqx.h2;

import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.LegacyTestDefinition.LegacyDBMemoryTest;
import io.github.zero88.jooqx.spi.h2.H2MemProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCLegacyHikariProvider;
import io.github.zero88.sample.model.h2.tables.Author;
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;
import io.vertx.junit5.VertxTestContext;

class H2LeGFailedTest extends LegacyDBMemoryTest<HikariCPDataSourceProvider>
    implements H2MemProvider, H2SQLHelper, JDBCLegacyHikariProvider {

    @Test
    void test_error_insert(VertxTestContext testContext) {
        final Author table = schema().AUTHOR;
        final String errorMsg = "Table \"AUTHOR\" not found; SQL statement:\ninsert into \"AUTHOR\" " +
                                "(\"ID\", \"FIRST_NAME\") values (default, ?) [42102-200]";
        jooqx.insert(dsl -> dsl.insertInto(table)
                               .columns(table.ID, table.FIRST_NAME)
                               .values(DSL.defaultValue(table.ID), DSL.value("abc")),
                     ar -> assertJooqException(testContext, ar, SQLStateClass.C42_SYNTAX_ERROR_OR_ACCESS_RULE_VIOLATION,
                                               errorMsg));
    }

}
