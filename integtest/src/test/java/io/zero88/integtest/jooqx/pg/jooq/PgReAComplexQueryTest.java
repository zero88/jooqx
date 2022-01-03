package io.zero88.integtest.jooqx.pg.jooq;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.zero88.integtest.jooqx.pg.PostgreSQLHelper.PgUseJooqType;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.jooqx.JsonRecord;
import io.zero88.sample.data.pgsql.Public;
import io.zero88.sample.data.pgsql.tables.pojos.Authors;
import io.zero88.sample.data.pgsql.tables.pojos.Books;
import io.zero88.sample.data.pgsql.tables.records.AuthorsRecord;
import io.zero88.sample.data.pgsql.tables.records.BooksAuthorsRecord;
import io.zero88.sample.data.pgsql.tables.records.BooksRecord;
import io.zero88.jooqx.spi.pg.PgPoolProvider;
import io.zero88.jooqx.spi.pg.PgSQLJooqxTest;

class PgReAComplexQueryTest extends PgSQLJooqxTest<PgPool> implements PgPoolProvider, PgUseJooqType {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql");
    }

    @Test
    void test_join_2_tables(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final DSLContext dsl = jooqx.dsl();
        final Public schema = schema().PUBLIC;
        final SelectConditionStep<Record> query = dsl.select(schema.AUTHORS.asterisk(), schema.BOOKS_AUTHORS.BOOK_ID)
                                                     .from(schema.AUTHORS)
                                                     .join(schema.BOOKS_AUTHORS)
                                                     .onKey()
                                                     .where(schema.AUTHORS.ID.eq(2));
        jooqx.execute(query, DSLAdapter.fetchJsonRecords(query.asTable()), ar -> ctx.verify(() -> {
            final List<JsonRecord<?>> records = assertResultSize(ctx, ar, 2);
            Assertions.assertEquals(
                new JsonObject("{\"id\":2,\"name\":\"F. Scott. Fitzgerald\",\"country\":\"USA\",\"book_id\":4}"),
                records.get(0).toJson());
            Assertions.assertEquals(
                new JsonObject("{\"id\":2,\"name\":\"F. Scott. Fitzgerald\",\"country\":\"USA\",\"book_id\":5}"),
                records.get(1).toJson());
            flag.flag();
        }));
    }

    @Test
    void test_join_2_tables_then_map_to_another_table(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final DSLContext dsl = jooqx.dsl();
        final Public schema = schema().PUBLIC;
        final SelectConditionStep<Record> query = dsl.select(schema.AUTHORS.asterisk(), schema.BOOKS_AUTHORS.BOOK_ID)
                                                     .from(schema.AUTHORS)
                                                     .join(schema.BOOKS_AUTHORS)
                                                     .onKey()
                                                     .where(schema.AUTHORS.ID.eq(4));
        jooqx.execute(query, DSLAdapter.fetchMany(query.asTable(), schema.AUTHORS), ar -> ctx.verify(() -> {
            final List<AuthorsRecord> records = assertResultSize(ctx, ar, 1);
            final AuthorsRecord authorsRecord = records.get(0);
            Assertions.assertEquals(4, authorsRecord.getId());
            Assertions.assertEquals("Scott Hanselman", authorsRecord.getName());
            Assertions.assertEquals("USA", authorsRecord.getCountry());
            flag.flag();
        }));
    }

    @Test
    void test_join_3_tables(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final DSLContext dsl = jooqx.dsl();
        final Public schema = schema().PUBLIC;
        final SelectConditionStep<Record> query = dsl.select(schema.AUTHORS.asterisk(), schema.BOOKS.ID.as("book_id"),
                                                             schema.BOOKS.TITLE.as("book_title"))
                                                     .from(schema.AUTHORS)
                                                     .join(schema.BOOKS_AUTHORS)
                                                     .on(schema.AUTHORS.ID.eq(schema.BOOKS_AUTHORS.AUTHOR_ID))
                                                     .join(schema.BOOKS)
                                                     .on(schema.BOOKS.ID.eq(schema.BOOKS_AUTHORS.BOOK_ID))
                                                     .where(schema.AUTHORS.ID.eq(1));
        jooqx.execute(query, DSLAdapter.fetchJsonRecords(query.asTable()), ar -> ctx.verify(() -> {
            final List<JsonRecord<?>> records = assertResultSize(ctx, ar, 3);
            final JsonRecord<?> record1 = records.get(0);
            Assertions.assertEquals(new JsonObject("{\"id\":1,\"name\":\"J.D. Salinger\",\"country\":\"USA\"," +
                                                   "\"book_id\":1,\"book_title\":\"The Catcher in the Rye\"}"),
                                    record1.toJson());
            Assertions.assertEquals(new JsonObject("{\"id\":1,\"name\":\"J.D. Salinger\",\"country\":\"USA\"," +
                                                   "\"book_id\":2,\"book_title\":\"Nine Stories\"}"),
                                    records.get(1).toJson());
            Assertions.assertEquals(new JsonObject("{\"id\":1,\"name\":\"J.D. Salinger\",\"country\":\"USA\"," +
                                                   "\"book_id\":3,\"book_title\":\"Franny and Zooey\"}"),
                                    records.get(2).toJson());
            final Authors author = record1.into(Authors.class);
            Assertions.assertEquals(author.getId(), 1);
            Assertions.assertEquals(author.getName(), "J.D. Salinger");
            Assertions.assertEquals(author.getCountry(), "USA");
            final Books book2 = record1.map(
                r -> new Books().setId(r.get(record1.getTable().field("book_id"), Integer.class))
                                .setTitle(r.get(record1.getTable().field("book_title"), String.class)));
            Assertions.assertEquals(book2.getId(), 1);
            Assertions.assertEquals(book2.getTitle(), "The Catcher in the Rye");
            flag.flag();
        }));
    }

    @Test
    void test_transaction_insert_into_3_tables(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final DSLContext dsl = jooqx.dsl();
        final Public schema = schema().PUBLIC;
        AuthorsRecord a1 = new AuthorsRecord().setName("Lukas").setCountry("Ger");
        BooksRecord b1 = new BooksRecord().setTitle("jOOQ doc");
        jooqx.transaction()
             .run(tx -> tx.execute(dsl.insertInto(schema.AUTHORS, schema.AUTHORS.NAME, schema.AUTHORS.COUNTRY)
                                      .values(a1.value2(), a1.value3())
                                      .returning(), DSLAdapter.fetchOne(schema.AUTHORS))
                          .flatMap(r1 -> tx.execute(
                              dsl.insertInto(schema.BOOKS, schema.BOOKS.TITLE).values(b1.value2()).returning(),
                              DSLAdapter.fetchOne(schema.BOOKS))
                                           .flatMap(r2 -> tx.execute(
                                               dsl.insertInto(schema.BOOKS_AUTHORS, schema.BOOKS_AUTHORS.BOOK_ID,
                                                              schema.BOOKS_AUTHORS.AUTHOR_ID)
                                                  .values(r2.getId(), r1.getId())
                                                  .returning(), DSLAdapter.fetchOne(schema.BOOKS_AUTHORS)))))
             .onComplete(ar -> ctx.verify(() -> {
                 final BooksAuthorsRecord record = assertSuccess(ctx, ar);
                 Assertions.assertNotNull(record);
                 Assertions.assertEquals(12, record.getId());
                 Assertions.assertEquals(9, record.getAuthorId());
                 Assertions.assertEquals(8, record.getBookId());
                 final SelectConditionStep<Record> query = dsl.select(schema.AUTHORS.asterisk(),
                                                                      schema.BOOKS.ID.as("book_id"),
                                                                      schema.BOOKS.TITLE.as("book_title"))
                                                              .from(schema.AUTHORS)
                                                              .join(schema.BOOKS_AUTHORS)
                                                              .on(schema.AUTHORS.ID.eq(schema.BOOKS_AUTHORS.AUTHOR_ID))
                                                              .join(schema.BOOKS)
                                                              .on(schema.BOOKS.ID.eq(schema.BOOKS_AUTHORS.BOOK_ID))
                                                              .where(schema.BOOKS_AUTHORS.ID.eq(ar.result().getId()));
                 jooqx.execute(query, DSLAdapter.fetchJsonRecord(query.asTable()), ar2 -> ctx.verify(() -> {
                     final JsonRecord<?> jsonRecord = assertSuccess(ctx, ar2);
                     Assertions.assertEquals(new JsonObject("{\"id\":9,\"name\":\"Lukas\",\"country\":\"Ger\"," +
                                                            "\"book_id\":8,\"book_title\":\"jOOQ doc\"}"),
                                             jsonRecord.toJson());
                     flag.flag();
                 }));
             }));
    }

}
