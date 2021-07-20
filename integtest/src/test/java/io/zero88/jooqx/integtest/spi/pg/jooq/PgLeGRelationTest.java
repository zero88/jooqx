package io.zero88.jooqx.integtest.spi.pg.jooq;

import java.util.Arrays;
import java.util.List;

import org.jooq.InsertResultStep;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.BindBatchValues;
import io.zero88.jooqx.JsonRecord;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.zero88.jooqx.integtest.pgsql.tables.Authors;
import io.zero88.jooqx.integtest.pgsql.tables.Books;
import io.zero88.jooqx.integtest.pgsql.tables.records.AuthorsRecord;
import io.zero88.jooqx.integtest.pgsql.tables.records.BooksRecord;
import io.zero88.jooqx.integtest.spi.pg.PostgreSQLHelper.PgLegacyType;
import io.zero88.jooqx.spi.pg.PgSQLLegacyTest;

class PgLeGRelationTest extends PgSQLLegacyTest implements PgLegacyType, JDBCErrorConverterProvider {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql");
    }

    @Test
    void test_query(VertxTestContext ctx) {
        final Books table = schema().BOOKS;
        jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchMany(table),
                      ar -> assertResultSize(ctx, ar, 7));
    }

    @Test
    void test_insert(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        final Books table = schema().BOOKS;
        final InsertResultStep<BooksRecord> insert = jooqx.dsl()
                                                          .insertInto(table, table.ID, table.TITLE)
                                                          .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                          .returning(table.ID);
        jooqx.execute(insert, DSLAdapter.fetchJsonRecord(table), ar -> ctx.verify(() -> {
            final JsonRecord<?> jsonRecord = assertSuccess(ctx, ar);
            Assertions.assertEquals(new JsonObject().put("id", 8).put("title", null), jsonRecord.toJson());
            flag.flag();
        }));
    }

    @Test
    void test_batch_insert(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Books table = schema().BOOKS;
        BooksRecord rec1 = new BooksRecord().setTitle("abc");
        BooksRecord rec2 = new BooksRecord().setTitle("xyz");
        BooksRecord rec3 = new BooksRecord().setTitle("qwe");

        final BindBatchValues bindValues = new BindBatchValues().register(table.TITLE).add(rec1, rec2, rec3);
        final InsertResultStep<BooksRecord> insert = jooqx.dsl()
                                                          .insertInto(table)
                                                          .set(bindValues.getDummyValues())
                                                          .returning();
        jooqx.batch(insert, bindValues, ar -> {
            ctx.verify(() -> Assertions.assertEquals(3, ar.result().getSuccesses()));
            jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchJsonRecords(table),
                          ar2 -> ctx.verify(() -> {
                              final List<JsonRecord<?>> records = assertResultSize(ctx, ar2, 10);
                              Assertions.assertEquals(new JsonObject().put("id", 8).put("title", "abc"),
                                                      records.get(7).toJson());
                              Assertions.assertEquals(new JsonObject().put("id", 9).put("title", "xyz"),
                                                      records.get(8).toJson());
                              Assertions.assertEquals(new JsonObject().put("id", 10).put("title", "qwe"),
                                                      records.get(9).toJson());
                              flag.flag();
                          }));
        });
    }

    @Test
    void test_transaction_multiple_update_but_one_failed(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Books table = schema().BOOKS;
        final Handler<AsyncResult<BooksRecord>> asserter = ar -> {
            assertJooqException(ctx, ar, SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION);
            jooqx.execute(jooqx.dsl().selectFrom(table).where(table.ID.eq(1)), DSLAdapter.fetchOne(table),
                          ar2 -> ctx.verify(() -> {
                              final BooksRecord record = assertSuccess(ctx, ar2);
                              Assertions.assertEquals("The Catcher in the Rye", record.getTitle());
                              flag.flag();
                          }));
        };
        jooqx.transaction()
             .run(tx -> tx.execute(tx.dsl()
                                     .update(table)
                                     .set(DSL.row(table.TITLE), DSL.row("something"))
                                     .where(table.ID.eq(1))
                                     .returning(), DSLAdapter.fetchOne(table))
                          .flatMap(r -> tx.execute(tx.dsl()
                                                     .update(table)
                                                     .set(DSL.row(table.TITLE), DSL.row((String) null))
                                                     .where(table.ID.eq(2))
                                                     .returning(), DSLAdapter.fetchOne(table))), asserter);
    }

    @Test
    void test_transaction_batch_insert_failed(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Authors table = schema().AUTHORS;
        AuthorsRecord i1 = new AuthorsRecord().setName("n1").setCountry("AT");
        AuthorsRecord i2 = new AuthorsRecord().setName("n2");
        final BindBatchValues bindValues = new BindBatchValues().register(table.NAME, table.COUNTRY).add(i1, i2);
        jooqx.transaction()
             .run(tx -> tx.batch(tx.dsl().insertInto(table).set(bindValues.getDummyValues()), bindValues), result -> {
                 assertJooqException(ctx, result, SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION,
                                     "Batch entry 1 insert into \"public\".\"authors\" (\"name\", " +
                                     "\"country\") values ('n2', NULL) was aborted: ERROR: null value" +
                                     " in column \"country\" violates not-null constraint\n" +
                                     "  Detail: Failing row contains (10, n2, null).  Call " +
                                     "getNextException to see other errors in the batch.");
                 jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchMany(table), ar2 -> {
                     assertResultSize(ctx, ar2, 8);
                     flag.flag();
                 });
             });
    }

}
