package io.github.zero88.integtest.jooqx.pg.jooq;

import java.util.Arrays;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.InsertResultStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectWhereStep;
import org.jooq.exception.TooManyRowsException;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JsonRecord;
import io.github.zero88.jooqx.spi.pg.PgConnProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.sample.model.pgsql.tables.pojos.Authors;
import io.github.zero88.sample.model.pgsql.tables.pojos.Books;
import io.github.zero88.sample.model.pgsql.tables.records.AuthorsRecord;
import io.github.zero88.sample.model.pgsql.tables.records.BooksRecord;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnection;

/**
 * If using v4.0.0, pretty sure thread leak, but v4.0.2 is already fixed
 * <a href="vertx-sql-client#909">https://github.com/eclipse-vertx/vertx-sql-client/issues/909</a>
 */
class PgReARelationTest extends PgSQLJooqxTest<PgConnection> implements PgConnProvider, PgUseJooqType {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql");
    }

    @Test
    void test_count(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.github.zero88.sample.model.pgsql.tables.Authors table = schema().AUTHORS;
        final SelectConditionStep<Record1<Integer>> query = jooqx.dsl()
                                                                 .selectCount()
                                                                 .from(table)
                                                                 .where(table.COUNTRY.eq("USA"));
        jooqx.execute(query, DSLAdapter.fetchCount(), ar -> ctx.verify(() -> {
            Assertions.assertEquals(6, assertSuccess(ctx, ar));
            flag.flag();
        }));
    }

    @Test
    void test_exist(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.github.zero88.sample.model.pgsql.tables.Authors table = schema().AUTHORS;
        final DSLContext dsl = jooqx.dsl();
        final SelectConditionStep<Record1<Integer>> q = dsl.selectOne()
                                                           .whereExists(dsl.selectFrom(table)
                                                                           .where(table.NAME.eq("Jane Austen")));
        jooqx.execute(q, DSLAdapter.fetchExists(), ar -> ctx.verify(() -> {
            Assertions.assertTrue(assertSuccess(ctx, ar));
            flag.flag();
        }));
    }

    @Test
    void test_insert_returning_id(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.github.zero88.sample.model.pgsql.tables.Books table = schema().BOOKS;
        final InsertResultStep<BooksRecord> insert = jooqx.dsl()
                                                          .insertInto(table, table.ID, table.TITLE)
                                                          .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                          .returning(table.ID);
        jooqx.execute(insert, DSLAdapter.fetchOne(table.ID), ar -> ctx.verify(() -> {
            final Record record = ar.result();
            final BooksRecord into1 = record.into(BooksRecord.class);
            Assertions.assertEquals(8, into1.getId());
            Assertions.assertNull(into1.getTitle());
            final Books into2 = record.into(Books.class);
            Assertions.assertEquals(8, into2.getId());
            Assertions.assertNull(into2.getTitle());
            final Authors into3 = record.into(Authors.class);
            Assertions.assertEquals(8, into3.getId());
            Assertions.assertNull(into3.getCountry());
            flag.flag();
        }));
    }

    @Test
    void test_select_one_convert_by_json_record(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.github.zero88.sample.model.pgsql.tables.Authors table = schema().AUTHORS;
        jooqx.fetchJsonRecord(dsl -> jooqx.dsl()
                                          .selectFrom(table)
                                          .where(table.COUNTRY.eq("USA"))
                                          .orderBy(table.NAME.desc())
                                          .limit(1)
                                          .offset(1), ar -> ctx.verify(() -> {
            final JsonRecord<AuthorsRecord> result = assertSuccess(ctx, ar);
            Assertions.assertNotNull(result);
            Assertions.assertEquals(new JsonObject("{\"id\":4,\"name\":\"Scott Hanselman\",\"country\":\"USA\"}"),
                                    result.toJson());
            flag.flag();
        }));
    }

    @Test
    void test_select_one_convert_by_json_object(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        jooqx.fetchJsonObject(dsl -> dsl.selectFrom(schema().AUTHORS).where(schema().AUTHORS.COUNTRY.eq("UK")).limit(1))
             .onSuccess(r -> ctx.verify(() -> {
                 Assertions.assertEquals(new JsonObject("{\"id\":3,\"name\":\"Jane Austen\",\"country\":\"UK\"}"), r);
                 flag.flag();
             }));
    }

    @Test
    void test_select_one_convert_by_table(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.github.zero88.sample.model.pgsql.tables.Authors table = schema().AUTHORS;
        final SelectConditionStep<AuthorsRecord> query = jooqx.dsl().selectFrom(table).where(table.COUNTRY.eq("UK"));
        jooqx.fetchOne(query, ar -> ctx.verify(() -> {
            Assertions.assertTrue(ar.succeeded());
            final AuthorsRecord authors = ar.result();
            Assertions.assertEquals(3, authors.getId());
            Assertions.assertEquals("Jane Austen", authors.getName());
            Assertions.assertEquals("UK", authors.getCountry());
            flag.flag();
        }));
    }

    @Test
    void test_select_one_but_give_query_that_returns_many(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.github.zero88.sample.model.pgsql.tables.Authors table = schema().AUTHORS;
        jooqx.fetchJsonRecord(dsl -> dsl.selectFrom(table).where(table.COUNTRY.eq("USA")).orderBy(table.ID.desc()))
             .onSuccess(event -> ctx.failNow("Should failed with TooManyRowsException"))
             .onFailure(t -> ctx.verify(() -> {
                 t.printStackTrace();
                 Assertions.assertTrue(t instanceof TooManyRowsException);
                 flag.flag();
             }));
    }

    @Test
    void test_select_many_convert_by_json_record(VertxTestContext ctx) {
        jooqx.fetchJsonRecords(dsl -> dsl.selectFrom(schema().BOOKS), ar -> assertResultSize(ctx, ar, 7));
    }

    @Test
    void test_select_jsonArray(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        jooqx.fetchJsonArray(dsl -> dsl.selectFrom(schema().AUTHORS).limit(3)).onSuccess(r -> ctx.verify(() -> {
            final JsonArray expected = new JsonArray(
                "[{\"id\":1,\"name\":\"J.D. Salinger\",\"country\":\"USA\"},{\"id\":2,\"name\":\"F. Scott. " +
                "Fitzgerald\",\"country\":\"USA\"},{\"id\":3,\"name\":\"Jane Austen\",\"country\":\"UK\"}]");
            Assertions.assertEquals(expected, r);
            flag.flag();
        })).onFailure(ctx::failNow);
    }

    @Test
    void test_select_many_convert_by_pojo_class(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.github.zero88.sample.model.pgsql.tables.Authors table = schema().AUTHORS;
        final SelectWhereStep<AuthorsRecord> query = jooqx.dsl().selectFrom(table);
        jooqx.execute(query, DSLAdapter.fetchMany(table, Authors.class), ar -> {
            final List<Authors> books = assertResultSize(ctx, ar, 8);
            final Authors authors = books.get(0);
            ctx.verify(() -> Assertions.assertEquals(1, authors.getId()));
            flag.flag();
        });
    }

}
