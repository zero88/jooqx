package io.zero88.jooqx.integtest.spi.pg;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.InsertResultStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnection;
import io.zero88.jooqx.JsonRecord;
import io.zero88.jooqx.ReactiveDSL;
import io.zero88.jooqx.integtest.pgsql.tables.records.AuthorsRecord;
import io.zero88.jooqx.integtest.pgsql.tables.records.BooksRecord;
import io.zero88.jooqx.spi.pg.PgConnProvider;
import io.zero88.jooqx.spi.pg.PgSQLReactiveTest;

/**
 * If using v4.0.0, pretty sure thread leak, but v4.0.2 is already fixed
 * <a href="vertx-sql-client#909">https://github.com/eclipse-vertx/vertx-sql-client/issues/909</a>
 */
class PgReAConnRelTest extends PgSQLReactiveTest<PgConnection> implements PgConnProvider, PostgreSQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_datatype/book_author.sql");
    }

    @Test
    void test_query(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.zero88.jooqx.integtest.pgsql.tables.Books table = catalog().PUBLIC.BOOKS;
        final SelectWhereStep<BooksRecord> query = jooqx.dsl().selectFrom(table);
        jooqx.execute(query, ReactiveDSL.adapter().fetchJsonRecords(table), ar -> assertResultSize(ctx, flag, ar, 7));
    }

    @Test
    void test_count(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.zero88.jooqx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        final SelectConditionStep<Record1<Integer>> query = jooqx.dsl()
                                                                 .selectCount()
                                                                 .from(table)
                                                                 .where(table.COUNTRY.eq("USA"));
        jooqx.execute(query, ReactiveDSL.adapter().fetchCount(query.asTable()), ar -> {
            ctx.verify(() -> Assertions.assertEquals(6, ar.result()));
            flag.flag();
        });
    }

    @Test
    void test_exist(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.zero88.jooqx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        final DSLContext dsl = jooqx.dsl();
        final SelectConditionStep<Record1<Integer>> q = dsl.selectOne()
                                                           .whereExists(dsl.selectFrom(table)
                                                                           .where(table.NAME.eq("Jane Austen")));
        jooqx.execute(q, ReactiveDSL.adapter().fetchExists(q.asTable()), ar -> {
            ctx.verify(() -> Assertions.assertTrue(ar.result()));
            flag.flag();
        });
    }

    @Test
    void test_select_one(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.zero88.jooqx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        final SelectForUpdateStep<AuthorsRecord> q = jooqx.dsl()
                                                          .selectFrom(table)
                                                          .where(table.COUNTRY.eq("USA"))
                                                          .orderBy(table.NAME.desc())
                                                          .limit(1)
                                                          .offset(1);
        jooqx.execute(q, ReactiveDSL.adapter().fetchJsonRecord(q.asTable()), ar -> {
            ctx.verify(() -> {
                final JsonRecord<?> result = ar.result();
                Assertions.assertNotNull(result);
                Assertions.assertEquals(new JsonObject("{\"id\":4,\"name\":\"Scott Hanselman\",\"country\":\"USA\"}"),
                                        result.toJson());
            });
            flag.flag();
        });
    }

    @Test
    void test_select_one_but_give_query_that_returns_many(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.zero88.jooqx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        final SelectForUpdateStep<AuthorsRecord> q = jooqx.dsl()
                                                          .selectFrom(table)
                                                          .where(table.COUNTRY.eq("USA"))
                                                          .orderBy(table.ID.desc());
        jooqx.execute(q, ReactiveDSL.adapter().fetchJsonRecord(q.asTable()), ar -> {
            ctx.verify(() -> {
                final JsonRecord<?> result = ar.result();
                Assertions.assertNotNull(result);
                Assertions.assertEquals(new JsonObject("{\"id\":8,\"name\":\"Christian Wenz\",\"country\":\"USA\"}"),
                                        result.toJson());
            });
            flag.flag();
        });
    }

    @Test
    void test_query_convert_by_record_class(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final io.zero88.jooqx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        final SelectWhereStep<AuthorsRecord> query = jooqx.dsl().selectFrom(table);
        jooqx.execute(query, ReactiveDSL.adapter()
                                        .fetchMany(table, io.zero88.jooqx.integtest.pgsql.tables.pojos.Authors.class),
                      ar -> {
                          final List<io.zero88.jooqx.integtest.pgsql.tables.pojos.Authors> books = assertResultSize(ctx,
                                                                                                                    flag,
                                                                                                                    ar,
                                                                                                                    8);
                          final io.zero88.jooqx.integtest.pgsql.tables.pojos.Authors authors = books.get(0);
                          ctx.verify(() -> Assertions.assertEquals(1, authors.getId()));
                          flag.flag();
                      });
    }

    @Test
    void test_query_convert_by_table(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.zero88.jooqx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        final SelectConditionStep<AuthorsRecord> query = jooqx.dsl().selectFrom(table).where(table.COUNTRY.eq("UK"));
        jooqx.execute(query, ReactiveDSL.adapter().fetchOne(table), ar -> {
            ctx.verify(() -> {
                Assertions.assertTrue(ar.succeeded());
                final AuthorsRecord authors = ar.result();
                Assertions.assertEquals(3, authors.getId());
                Assertions.assertEquals("Jane Austen", authors.getName());
                Assertions.assertEquals("UK", authors.getCountry());
            });
            flag.flag();
        });
    }

    @Test
    void test_insert_returning_id(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.zero88.jooqx.integtest.pgsql.tables.Books table = catalog().PUBLIC.BOOKS;
        final InsertResultStep<BooksRecord> insert = jooqx.dsl()
                                                          .insertInto(table, table.ID, table.TITLE)
                                                          .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                          .returning(table.ID);
        jooqx.execute(insert, ReactiveDSL.adapter().fetchOne(table, Collections.singletonList(table.ID)), ar -> {
            ctx.verify(() -> {
                final Record record = ar.result();
                final BooksRecord into1 = record.into(BooksRecord.class);
                Assertions.assertEquals(8, into1.getId());
                Assertions.assertNull(into1.getTitle());
                final io.zero88.jooqx.integtest.pgsql.tables.pojos.Books into2 = record.into(
                    io.zero88.jooqx.integtest.pgsql.tables.pojos.Books.class);
                Assertions.assertEquals(8, into2.getId());
                Assertions.assertNull(into2.getTitle());
                final io.zero88.jooqx.integtest.pgsql.tables.pojos.Authors into3 = record.into(
                    io.zero88.jooqx.integtest.pgsql.tables.pojos.Authors.class);
                Assertions.assertEquals(8, into3.getId());
                Assertions.assertNull(into3.getCountry());
            });

            flag.flag();
        });
    }

}
