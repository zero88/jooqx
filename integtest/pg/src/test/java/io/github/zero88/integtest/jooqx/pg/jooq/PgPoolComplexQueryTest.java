package io.github.zero88.integtest.jooqx.pg.jooq;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JsonRecord;
import io.github.zero88.jooqx.spi.pg.PgPoolProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.sample.model.pgsql.Public;
import io.github.zero88.sample.model.pgsql.tables.pojos.Authors;
import io.github.zero88.sample.model.pgsql.tables.pojos.Books;
import io.github.zero88.sample.model.pgsql.tables.records.AuthorsRecord;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;

class PgPoolComplexQueryTest extends PgSQLJooqxTest<PgPool> implements PgPoolProvider, PgUseJooqType {

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
            final List<JsonRecord<Record>> records = assertResultSize(ctx, ar, 2);
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
        jooqx.fetchJsonRecords(query, ar -> ctx.verify(() -> {
            final List<JsonRecord<Record>> records = assertResultSize(ctx, ar, 3);
            final JsonRecord<Record> record1 = records.get(0);
            Assertions.assertEquals(new JsonObject("{\"id\":1,\"name\":\"J.D. Salinger\",\"country\":\"USA\"," +
                                                   "\"book_id\":1,\"book_title\":\"The Catcher in the Rye\"}"),
                                    record1.toJson());
            Assertions.assertEquals(new JsonObject("{\"id\":1,\"name\":\"J.D. Salinger\",\"country\":\"USA\"," +
                                                   "\"book_id\":2,\"book_title\":\"Nine Stories\"}"),
                                    records.get(1).toJson());
            Assertions.assertEquals(new JsonObject("{\"id\":1,\"name\":\"J.D. Salinger\",\"country\":\"USA\"," +
                                                   "\"book_id\":3,\"book_title\":\"Franny and Zooey\"}"),
                                    records.get(2).toJson());
            final Authors author = record1.record().into(Authors.class);
            Assertions.assertEquals(author.getId(), 1);
            Assertions.assertEquals(author.getName(), "J.D. Salinger");
            Assertions.assertEquals(author.getCountry(), "USA");
            final Books book2 = record1.record()
                                       .map(r -> new Books().setId(r.get("book_id", Integer.class))
                                                            .setTitle(r.get("book_title", String.class)));
            Assertions.assertEquals(book2.getId(), 1);
            Assertions.assertEquals(book2.getTitle(), "The Catcher in the Rye");
            flag.flag();
        }));
    }

}
