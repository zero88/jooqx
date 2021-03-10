package io.github.zero88.jooq.vertx.integtest.jdbc;

import java.util.Arrays;

import org.jooq.InsertResultStep;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.zero88.jooq.vertx.LegacyDSLAdapter;
import io.github.zero88.jooq.vertx.LegacySQLTest.LegacyDBMemoryTest;
import io.github.zero88.jooq.vertx.SQLErrorConverter;
import io.github.zero88.jooq.vertx.integtest.H2SQLHelper;
import io.github.zero88.jooq.vertx.integtest.h2.tables.Author;
import io.github.zero88.jooq.vertx.integtest.h2.tables.records.AuthorRecord;
import io.github.zero88.jooq.vertx.spi.H2DBProvider;
import io.github.zero88.jooq.vertx.spi.JDBCErrorConverter;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class H2LegacyFailedTest extends LegacyDBMemoryTest implements H2DBProvider, H2SQLHelper {

    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
    }

    @Override
    public SQLErrorConverter<? extends Throwable, ? extends RuntimeException> createErrorConverter() {
        return new JDBCErrorConverter();
    }

    @Test
    void test(VertxTestContext testContext) {
        final Checkpoint flag = testContext.checkpoint(1);
        final Author table = catalog().DEFAULT_SCHEMA.AUTHOR;
        final InsertResultStep<AuthorRecord> insert = executor.dsl()
                                                              .insertInto(table, table.ID, table.FIRST_NAME)
                                                              .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                              .returning(table.ID);
        executor.execute(insert, LegacyDSLAdapter.instance().fetchJsonRecord(table),
                         ar -> assertJooqException(testContext, flag, ar,
                                                   SQLStateClass.C42_SYNTAX_ERROR_OR_ACCESS_RULE_VIOLATION,
                                                   "Table \"AUTHOR\" not found; SQL statement:\n" +
                                                   "insert into \"AUTHOR\" (\"ID\", \"FIRST_NAME\") values (default, ?) " +
                                                   "[42102-200]"));
    }

}
