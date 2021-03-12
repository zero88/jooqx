package io.zero88.jooqx.integtest.spi.h2;

import java.util.Arrays;

import org.jooq.InsertResultStep;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.LegacyDSL;
import io.zero88.jooqx.LegacyTestDefinition.LegacyDBMemoryTest;
import io.zero88.jooqx.UseJdbcErrorConverter;
import io.zero88.jooqx.integtest.h2.tables.Author;
import io.zero88.jooqx.integtest.h2.tables.records.AuthorRecord;
import io.zero88.jooqx.spi.h2.H2DBProvider;
import io.zero88.jooqx.spi.jdbc.JDBCLegacyHikariProvider;

class H2LeGFailedTest extends LegacyDBMemoryTest<HikariCPDataSourceProvider>
    implements H2DBProvider, H2SQLHelper, JDBCLegacyHikariProvider, UseJdbcErrorConverter {

    @Test
    void test(VertxTestContext testContext) {
        final Author table = catalog().DEFAULT_SCHEMA.AUTHOR;
        final InsertResultStep<AuthorRecord> insert = jooqx.dsl()
                                                           .insertInto(table, table.ID, table.FIRST_NAME)
                                                           .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                           .returning(table.ID);
        jooqx.execute(insert, LegacyDSL.adapter().fetchJsonRecord(table),
                      ar -> assertJooqException(testContext, ar,
                                                SQLStateClass.C42_SYNTAX_ERROR_OR_ACCESS_RULE_VIOLATION,
                                                "Table \"AUTHOR\" not found; SQL statement:\n" +
                                                "insert into \"AUTHOR\" (\"ID\", \"FIRST_NAME\") values (default, ?) " +
                                                "[42102-200]"));
    }

}
