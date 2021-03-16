package io.zero88.jooqx.integtest.spi.pg;

import java.util.Arrays;
import java.util.Collections;

import org.jooq.InsertResultStep;
import org.jooq.SelectConditionStep;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnection;
import io.vertx.pgclient.PgException;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.jooqx.integtest.pgsql.tables.Books;
import io.zero88.jooqx.integtest.pgsql.tables.records.BooksRecord;
import io.zero88.jooqx.spi.pg.PgConnProvider;
import io.zero88.jooqx.spi.pg.PgSQLReactiveTest;
import io.zero88.jooqx.spi.pg.UsePgSQLErrorConverter;

class PgReAFailedTest extends PgSQLReactiveTest<PgConnection>
    implements PgConnProvider, PostgreSQLHelper, UsePgSQLErrorConverter {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql");
    }

    @Test
    void test_insert_failed(VertxTestContext ctx) {
        final Books table = catalog().PUBLIC.BOOKS;
        final InsertResultStep<BooksRecord> insert = jooqx.dsl()
                                                          .insertInto(table, table.ID, table.TITLE)
                                                          .values(1, "abc")
                                                          .returning(table.ID);
        jooqx.execute(insert, DSLAdapter.fetchOne(table, Collections.singletonList(table.ID)),
                      ar -> assertJooqException(ctx, ar, SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION,
                                                "duplicate key value violates unique constraint \"books_pkey\"",
                                                PgException.class));
    }

    @Test
    void test_select_none_exist(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Books table = catalog().PUBLIC.BOOKS;
        final SelectConditionStep<BooksRecord> insert = jooqx.dsl().selectFrom(table).where(table.ID.eq(1000));
        jooqx.execute(insert, DSLAdapter.fetchOne(table, Collections.singletonList(table.ID)),
                      ar -> ctx.verify(() -> {
                          Assertions.assertNull(assertSuccess(ctx, ar));
                          flag.flag();
                      }));
    }

    @Test
    void transaction_failed_due_to_unsupported(VertxTestContext context) {
        final Checkpoint flag = context.checkpoint();
        final Books table = catalog().PUBLIC.BOOKS;
        final InsertResultStep<BooksRecord> q = jooqx.dsl()
                                                     .insertInto(table, table.ID, table.TITLE)
                                                     .values(Arrays.asList(DSL.defaultValue(), "1"))
                                                     .returning();
        jooqx.transaction().run(tx -> tx.execute(q, DSLAdapter.fetchOne(table)), async -> {
            assertJooqException(context, async, SQLStateClass.C08_CONNECTION_EXCEPTION,
                                "Unsupported using connection on SQL connection: [class io.vertx.pgclient.impl" +
                                ".PgConnectionImpl]. Switch using SQL pool");
            jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchMany(table), ar2 -> {
                assertResultSize(context, ar2, 7);
                flag.flag();
            });
        });
    }

}
