package io.github.zero88.jooq.vertx.integtest.jdbc;

import java.util.Arrays;

import org.jooq.InsertResultStep;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.github.zero88.jooq.vertx.BaseLegacySqlTest.AbstractLegacyMemoryTest;
import io.github.zero88.jooq.vertx.SqlErrorMaker;
import io.github.zero88.jooq.vertx.adapter.ListResultAdapter;
import io.github.zero88.jooq.vertx.converter.LegacyResultSetConverter;
import io.github.zero88.jooq.vertx.integtest.H2SQLHelper;
import io.github.zero88.jooq.vertx.integtest.h2.tables.Author;
import io.github.zero88.jooq.vertx.integtest.h2.tables.records.AuthorRecord;
import io.github.zero88.jooq.vertx.spi.H2DBProvider;
import io.github.zero88.jooq.vertx.spi.SqlJdbcErrorMaker;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;

@ExtendWith(VertxExtension.class)
public class H2LegacyJdbcFailedTest extends AbstractLegacyMemoryTest implements H2DBProvider, H2SQLHelper {

    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
    }

    @Override
    public SqlErrorMaker<? extends Throwable, ? extends RuntimeException> createErrorMaker() {
        return new SqlJdbcErrorMaker();
    }

    @Test
    void test(VertxTestContext testContext) {
        final Checkpoint flag = testContext.checkpoint(1);
        final Author table = catalog().DEFAULT_SCHEMA.AUTHOR;
        final InsertResultStep<AuthorRecord> insert = executor.dsl()
                                                              .insertInto(table, table.ID, table.FIRST_NAME)
                                                              .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                              .returning(table.ID);
        executor.execute(insert, ListResultAdapter.createVertxRecord(table, new LegacyResultSetConverter()), ar -> {
            testContext.verify(() -> {
                Assertions.assertTrue(ar.cause() instanceof DataAccessException);
                final DataAccessException cause = (DataAccessException) ar.cause();
                Assertions.assertEquals(SQLStateClass.C42_SYNTAX_ERROR_OR_ACCESS_RULE_VIOLATION, cause.sqlStateClass());
                Assertions.assertEquals("42S02", cause.sqlState());
                Assertions.assertEquals("Table \"AUTHOR\" not found; SQL statement:\n" +
                                        "insert into \"AUTHOR\" (\"ID\", \"FIRST_NAME\") values (default, ?) " +
                                        "[42102-200]", cause.getMessage());
            });

            flag.flag();
        });
    }

}
