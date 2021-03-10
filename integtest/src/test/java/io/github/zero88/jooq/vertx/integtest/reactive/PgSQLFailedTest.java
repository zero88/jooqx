package io.github.zero88.jooq.vertx.integtest.reactive;

import java.util.Arrays;
import java.util.Collections;

import org.jooq.InsertResultStep;
import org.jooq.SelectConditionStep;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooq.vertx.SQLErrorConverter;
import io.github.zero88.jooq.vertx.ReactiveDSLAdapter;
import io.github.zero88.jooq.vertx.integtest.PostgreSQLHelper;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.records.BooksRecord;
import io.github.zero88.jooq.vertx.spi.PgErrorConverter;
import io.github.zero88.jooq.vertx.spi.PostgreSQLReactiveTest.PostgreSQLClientTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgException;

class PgSQLFailedTest extends PostgreSQLClientTest implements PostgreSQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt);
    }

    @Override
    public SQLErrorConverter<? extends Throwable, ? extends RuntimeException> createErrorConverter() {
        return new PgErrorConverter();
    }

    @Test
    void test_insert_failed(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Books table = catalog().PUBLIC.BOOKS;
        final InsertResultStep<BooksRecord> insert = executor.dsl()
                                                             .insertInto(table, table.ID, table.TITLE)
                                                             .values(1, "abc")
                                                             .returning(table.ID);
        executor.execute(insert, ReactiveDSLAdapter.instance().fetchOne(table, Collections.singletonList(table.ID)),
                         ar -> {
                             ctx.verify(() -> {
                                 Assertions.assertTrue(ar.failed());
                                 final Throwable cause = ar.cause();
                                 Assertions.assertTrue(cause instanceof DataAccessException);
                                 Assertions.assertEquals(
                                     "duplicate key value violates unique constraint \"books_pkey\"",
                                     cause.getMessage());
                                 Assertions.assertEquals("23505", ((DataAccessException) cause).sqlState());
                                 Assertions.assertEquals(SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION,
                                                         ((DataAccessException) cause).sqlStateClass());
                                 Assertions.assertNotNull(((DataAccessException) cause).getCause(PgException.class));
                             });
                             flag.flag();
                         });
    }

    @Test
    void test_select_none_exist(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Books table = catalog().PUBLIC.BOOKS;
        final SelectConditionStep<BooksRecord> insert = executor.dsl().selectFrom(table).where(table.ID.eq(1000));
        executor.execute(insert, ReactiveDSLAdapter.instance().fetchOne(table, Collections.singletonList(table.ID)),
                         ar -> {
                             ctx.verify(() -> {
                                 Assertions.assertTrue(ar.succeeded());
                                 Assertions.assertNull(ar.result());
                             });
                             flag.flag();
                         });
    }

    @Test
    void transaction_failed_due_to_unsupported(VertxTestContext context) {
        final Checkpoint flag = context.checkpoint(2);
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Books table = catalog().PUBLIC.BOOKS;
        final InsertResultStep<BooksRecord> q = executor.dsl()
                                                        .insertInto(table, table.ID, table.TITLE)
                                                        .values(Arrays.asList(DSL.defaultValue(), "1"))
                                                        .returning();
        executor.transaction().run(tx -> tx.execute(q, ReactiveDSLAdapter.instance().fetchOne(table)), async -> {
            context.verify(() -> {
                Assertions.assertTrue(async.failed());
                Assertions.assertTrue(async.cause() instanceof DataAccessException);
                Assertions.assertEquals(SQLStateClass.C08_CONNECTION_EXCEPTION,
                                        ((DataAccessException) async.cause()).sqlStateClass());
                Assertions.assertEquals(
                    "Unsupported using connection on SQL connection: [class io.vertx.pgclient.impl.PgConnectionImpl]." +
                    " Switch using SQL pool", async.cause().getMessage());
                executor.execute(executor.dsl().selectFrom(table), ReactiveDSLAdapter.instance().fetchMany(table),
                                 ar2 -> assertRsSize(context, flag, ar2, 7));
            });
            flag.flag();
        });
    }

}
