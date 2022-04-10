package io.github.zero88.integtest.jooqx.h2;

import java.util.Arrays;

import org.jooq.InsertResultStep;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JooqxTestDefinition.JooqxDBMemoryTest;
import io.github.zero88.jooqx.spi.h2.H2MemProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.VertxTestContext;
import io.zero88.sample.data.h2.tables.Author;
import io.zero88.sample.data.h2.tables.records.AuthorRecord;

class H2ReAFailedTest extends JooqxDBMemoryTest<JDBCPool>
    implements H2MemProvider, H2SQLHelper, JDBCPoolHikariProvider {

    @Test
    void test(VertxTestContext testContext) {
        final Author table = schema().AUTHOR;
        final InsertResultStep<AuthorRecord> insert = jooqx.dsl()
                                                           .insertInto(table, table.ID, table.FIRST_NAME)
                                                           .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                           .returning(table.ID);
        jooqx.execute(insert, DSLAdapter.fetchJsonRecord(table), ar -> {
            final String errorMsg = "Table \"AUTHOR\" not found; SQL statement:\n" + "insert into \"AUTHOR\" " +
                                    "(\"ID\", \"FIRST_NAME\") values (default, ?) [42102-200]";
            assertJooqException(testContext, ar, SQLStateClass.C42_SYNTAX_ERROR_OR_ACCESS_RULE_VIOLATION, errorMsg);
        });
    }

}