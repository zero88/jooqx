package io.zero88.jooqx.integtest.jdbc;

import java.util.Arrays;
import java.util.List;

import org.jooq.InsertResultStep;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.BindBatchValues;
import io.zero88.jooqx.JooqErrorConverter.JDBCErrorConverter;
import io.zero88.jooqx.JsonRecord;
import io.zero88.jooqx.LegacyDSL;
import io.zero88.jooqx.LegacySQLTest.LegacyDBContainerTest;
import io.zero88.jooqx.SQLErrorConverter;
import io.zero88.jooqx.integtest.PostgreSQLHelper;
import io.zero88.jooqx.integtest.pgsql.tables.Authors;
import io.zero88.jooqx.integtest.pgsql.tables.Books;
import io.zero88.jooqx.integtest.pgsql.tables.records.AuthorsRecord;
import io.zero88.jooqx.integtest.pgsql.tables.records.BooksRecord;
import io.zero88.jooqx.spi.PostgreSQLLegacyTest;

class PgSQLLegacyTest extends LegacyDBContainerTest<PostgreSQLContainer<?>>
    implements PostgreSQLLegacyTest, PostgreSQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt);
    }

    @Override
    public SQLErrorConverter<? extends Throwable, ? extends RuntimeException> createErrorConverter() {
        return new JDBCErrorConverter();
    }

    @Test
    void test_query(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Books table = catalog().PUBLIC.BOOKS;
        executor.execute(executor.dsl().selectFrom(table), LegacyDSL.adapter().fetchMany(table),
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
        executor.execute(insert, LegacyDSL.adapter().fetchJsonRecord(table), ar -> {
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
        executor.batch(insert, bindValues, ar -> {
            ctx.verify(() -> Assertions.assertEquals(3, ar.result().getSuccesses()));
            flag.flag();
            executor.execute(executor.dsl().selectFrom(table), LegacyDSL.adapter().fetchJsonRecords(table),
                             ar2 -> {
                                 final List<JsonRecord<?>> records = assertRsSize(ctx, flag, ar2, 10);
                                 ctx.verify(() -> {
                                     Assertions.assertEquals(new JsonObject().put("id", 8).put("title", "abc"),
                                                             records.get(7).toJson());
                                     Assertions.assertEquals(new JsonObject().put("id", 9).put("title", "xyz"),
                                                             records.get(8).toJson());
                                     Assertions.assertEquals(new JsonObject().put("id", 10).put("title", "qwe"),
                                                             records.get(9).toJson());
                                 });
                                 flag.flag();
                             });
        });
    }

    @Test
    void test_transaction_multiple_update_but_one_failed(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final Books table = catalog().PUBLIC.BOOKS;
        executor.transaction()
                .run(tx -> tx.execute(tx.dsl()
                                        .update(table)
                                        .set(DSL.row(table.TITLE), DSL.row("something"))
                                        .where(table.ID.eq(1))
                                        .returning(), LegacyDSL.adapter().fetchOne(table))
                             .flatMap(r -> tx.execute(tx.dsl()
                                                        .update(table)
                                                        .set(DSL.row(table.TITLE), DSL.row((String) null))
                                                        .where(table.ID.eq(2))
                                                        .returning(), LegacyDSL.adapter().fetchOne(table))),
                     ar -> {
                         assertJooqException(ctx, flag, ar, SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION);
                         executor.execute(executor.dsl().selectFrom(table).where(table.ID.eq(1)),
                                          LegacyDSL.adapter().fetchOne(table), ar2 -> {
                                 ctx.verify(
                                     () -> Assertions.assertEquals("The Catcher in the Rye", ar2.result().getTitle()));
                                 flag.flag();
                             });
                     });
    }

    @Test
    void test_transaction_batch_insert_failed(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final Authors table = catalog().PUBLIC.AUTHORS;
        AuthorsRecord i1 = new AuthorsRecord().setName("n1").setCountry("AT");
        AuthorsRecord i2 = new AuthorsRecord().setName("n2");
        final BindBatchValues bindValues = new BindBatchValues().register(table.NAME, table.COUNTRY).add(i1, i2);
        executor.transaction()
                .run(tx -> tx.batch(tx.dsl().insertInto(table).set(bindValues.getDummyValues()), bindValues),
                     result -> {
                         assertJooqException(ctx, flag, result, SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION,
                                             "Batch entry 1 insert into \"public\".\"authors\" (\"name\", " +
                                             "\"country\") values ('n2', NULL) was aborted: ERROR: null value" +
                                             " in column \"country\" violates not-null constraint\n" +
                                             "  Detail: Failing row contains (10, n2, null).  Call " +
                                             "getNextException to see other errors in the batch.");
                         executor.execute(executor.dsl().selectFrom(table),
                                          LegacyDSL.adapter().fetchMany(table),
                                          ar2 -> assertRsSize(ctx, flag, ar2, 8));
                     });
    }

}
