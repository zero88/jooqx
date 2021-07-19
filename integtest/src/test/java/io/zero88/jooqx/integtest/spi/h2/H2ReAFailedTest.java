package io.zero88.jooqx.integtest.spi.h2;

import java.util.Arrays;

import org.jooq.InsertResultStep;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.jooqx.ReactiveTestDefinition.ReactiveDBMemoryTest;
import io.zero88.jooqx.UseJdbcErrorConverter;
import io.zero88.jooqx.integtest.h2.tables.Author;
import io.zero88.jooqx.integtest.h2.tables.records.AuthorRecord;
import io.zero88.jooqx.spi.h2.H2DBMemProvider;
import io.zero88.jooqx.spi.jdbc.JDBCReactiveProvider;

class H2ReAFailedTest extends ReactiveDBMemoryTest<JDBCPool>
    implements H2DBMemProvider, H2SQLHelper, JDBCReactiveProvider, UseJdbcErrorConverter {

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
