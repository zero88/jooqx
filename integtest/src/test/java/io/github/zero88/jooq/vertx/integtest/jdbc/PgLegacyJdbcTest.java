package io.github.zero88.jooq.vertx.integtest.jdbc;

import java.util.Arrays;
import java.util.List;

import org.jooq.InsertResultStep;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import io.github.zero88.jooq.vertx.BaseLegacySqlTest.AbstractLegacyDBCTest;
import io.github.zero88.jooq.vertx.BindBatchValues;
import io.github.zero88.jooq.vertx.VertxJooqRecord;
import io.github.zero88.jooq.vertx.VertxLegacyDSL;
import io.github.zero88.jooq.vertx.integtest.PostgreSQLHelper;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.Books;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.records.BooksRecord;
import io.github.zero88.jooq.vertx.spi.PostgreSQLJdbcTest;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class PgLegacyJdbcTest extends AbstractLegacyDBCTest<PostgreSQLContainer<?>>
    implements PostgreSQLJdbcTest, PostgreSQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt);
    }

    @Test
    void test_query(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Books table = catalog().PUBLIC.BOOKS;
        executor.execute(executor.dsl().selectFrom(table), VertxLegacyDSL.instance().fetchMany(table, table),
                         ar -> assertRsSize(ctx, flag, ar, 7));
    }

    @Test
    void test_insert(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(1);
        final Books table = catalog().PUBLIC.BOOKS;
        final InsertResultStep<BooksRecord> insert = executor.dsl()
                                                             .insertInto(table, table.ID, table.TITLE)
                                                             .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                             .returning(table.ID);
        executor.execute(insert, VertxLegacyDSL.instance().fetchVertxRecord(table), ar -> {
            ctx.verify(
                () -> Assertions.assertEquals(new JsonObject().put("id", 8).put("title", null), ar.result().toJson()));
            flag.flag();
        });
    }

    @Test
    void test_batch_insert(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(3);
        final Books table = catalog().PUBLIC.BOOKS;
        BooksRecord rec1 = new BooksRecord().setTitle("abc");
        BooksRecord rec2 = new BooksRecord().setTitle("xyz");
        BooksRecord rec3 = new BooksRecord().setTitle("qwe");

        final BindBatchValues bindValues = new BindBatchValues().register(table.TITLE).add(rec1, rec2, rec3);
        final InsertResultStep<BooksRecord> insert = executor.dsl()
                                                             .insertInto(table)
                                                             .set(bindValues.getDummyValues())
                                                             .returning();
        executor.batchExecute(insert, bindValues, ar -> {
            ctx.verify(() -> Assertions.assertEquals(3, ar.result().getSuccesses()));
            flag.flag();
        });
        executor.execute(executor.dsl().selectFrom(table), VertxLegacyDSL.instance().fetchVertxRecords(table), ar -> {
            final List<VertxJooqRecord<?>> records = assertRsSize(ctx, flag, ar, 10);
            ctx.verify(() -> {
                Assertions.assertEquals(new JsonObject().put("id", 8).put("title", "abc"), records.get(7).toJson());
                Assertions.assertEquals(new JsonObject().put("id", 9).put("title", "xyz"), records.get(8).toJson());
                Assertions.assertEquals(new JsonObject().put("id", 10).put("title", "qwe"), records.get(9).toJson());
            });
            flag.flag();
        });
    }

}
