package io.zero88.integtest.jooqx.pg.jooq;

import java.util.Collections;

import org.jooq.InsertResultStep;
import org.jooq.SelectConditionStep;
import org.jooq.exception.SQLStateClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnection;
import io.vertx.pgclient.PgException;
import io.zero88.integtest.jooqx.pg.PostgreSQLHelper.PgUseJooqType;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.jooqx.spi.pg.PgConnProvider;
import io.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.zero88.sample.data.pgsql.tables.Books;
import io.zero88.sample.data.pgsql.tables.records.BooksRecord;

class PgReAFailedTest extends PgSQLJooqxTest<PgConnection>
    implements PgConnProvider, PgUseJooqType, PgSQLErrorConverterProvider {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql");
    }

    @Test
    void test_insert_failed(VertxTestContext ctx) {
        final Books table = schema().BOOKS;
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
        final Books table = schema().BOOKS;
        final SelectConditionStep<BooksRecord> insert = jooqx.dsl().selectFrom(table).where(table.ID.eq(1000));
        jooqx.execute(insert, DSLAdapter.fetchOne(table, Collections.singletonList(table.ID)), ar -> ctx.verify(() -> {
            Assertions.assertNull(assertSuccess(ctx, ar));
            flag.flag();
        }));
    }

    @Test
    void transaction_failed_due_to_unsupported() {
        Assertions.assertThrows(UnsupportedOperationException.class, jooqx::transaction,
                                "Unsupported using connection on SQL connection: [class io.vertx.pgclient.impl" +
                                ".PgConnectionImpl]. Switch using SQL pool");
    }

}
