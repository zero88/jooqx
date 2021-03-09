package io.github.zero88.jooq.vertx.integtest.reactive;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.DSLContext;
import org.jooq.InsertResultStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooq.vertx.BatchResult;
import io.github.zero88.jooq.vertx.BatchReturningResult;
import io.github.zero88.jooq.vertx.BindBatchValues;
import io.github.zero88.jooq.vertx.VertxJooqRecord;
import io.github.zero88.jooq.vertx.VertxReactiveDSL;
import io.github.zero88.jooq.vertx.adapter.SelectListResultAdapter;
import io.github.zero88.jooq.vertx.converter.ReactiveResultBatchConverter;
import io.github.zero88.jooq.vertx.converter.ReactiveResultSetConverter;
import io.github.zero88.jooq.vertx.integtest.PostgreSQLHelper;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.pojos.Authors;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.pojos.Books;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.records.AuthorsRecord;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.records.BooksRecord;
import io.github.zero88.jooq.vertx.spi.PostgreSQLReactiveTest.AbstractPostgreSQLReactiveTest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

/**
 * If using v4.0.0, pretty sure thread leak, but v4.0.2 is already fixed
 * <a href="vertx-sql-client#909">https://github.com/eclipse-vertx/vertx-sql-client/issues/909</a>
 */
class PgJooqSuccessTest extends AbstractPostgreSQLReactiveTest implements PostgreSQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt);
    }

    @Test
    void test_query(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Books table = catalog().PUBLIC.BOOKS;
        final SelectWhereStep<BooksRecord> query = executor.dsl().selectFrom(table);
        executor.execute(query, VertxReactiveDSL.instance().fetchVertxRecords(table),
                         ar -> assertRsSize(ctx, flag, ar, 7));
    }

    @Test
    void test_count(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        final SelectConditionStep<Record1<Integer>> query = executor.dsl()
                                                                    .selectCount()
                                                                    .from(table)
                                                                    .where(table.COUNTRY.eq("USA"));
        executor.execute(query, VertxReactiveDSL.instance().fetchCount(query.asTable()), ar -> {
            ctx.verify(() -> Assertions.assertEquals(6, ar.result()));
            flag.flag();
        });
    }

    @Test
    void test_exist(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        final DSLContext dsl = executor.dsl();
        final SelectConditionStep<Record1<Integer>> q = dsl.selectOne()
                                                           .whereExists(dsl.selectFrom(table)
                                                                           .where(table.NAME.eq("Jane Austen")));
        executor.execute(q, VertxReactiveDSL.instance().fetchExists(q.asTable()), ar -> {
            ctx.verify(() -> Assertions.assertTrue(ar.result()));
            flag.flag();
        });
    }

    @Test
    void test_select_one(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        final SelectForUpdateStep<AuthorsRecord> q = executor.dsl()
                                                             .selectFrom(table)
                                                             .where(table.COUNTRY.eq("USA"))
                                                             .orderBy(table.NAME.desc())
                                                             .limit(1)
                                                             .offset(1);
        executor.execute(q, VertxReactiveDSL.instance().fetchVertxRecord(q.asTable()), ar -> {
            ctx.verify(() -> {
                final VertxJooqRecord<?> result = ar.result();
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
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        final SelectForUpdateStep<AuthorsRecord> q = executor.dsl()
                                                             .selectFrom(table)
                                                             .where(table.COUNTRY.eq("USA"))
                                                             .orderBy(table.ID.desc());
        executor.execute(q, VertxReactiveDSL.instance().fetchVertxRecord(q.asTable()), ar -> {
            ctx.verify(() -> {
                final VertxJooqRecord<?> result = ar.result();
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
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        final SelectWhereStep<AuthorsRecord> query = executor.dsl().selectFrom(table);
        executor.execute(query, VertxReactiveDSL.instance().fetchMany(table, Authors.class), ar -> {
            final List<Authors> books = assertRsSize(ctx, flag, ar, 8);
            final Authors authors = books.get(0);
            ctx.verify(() -> Assertions.assertEquals(1, authors.getId()));
            flag.flag();
        });
    }

    @Test
    void test_query_convert_by_table_class(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        final SelectConditionStep<AuthorsRecord> query = executor.dsl().selectFrom(table).where(table.COUNTRY.eq("UK"));
        executor.execute(query, SelectListResultAdapter.create(table, new ReactiveResultSetConverter(), table), ar -> {
            final List<AuthorsRecord> books = assertRsSize(ctx, flag, ar, 1);
            final AuthorsRecord authors = books.get(0);
            ctx.verify(() -> Assertions.assertEquals(3, authors.getId()));
            ctx.verify(() -> Assertions.assertEquals("Jane Austen", authors.getName()));
            ctx.verify(() -> Assertions.assertEquals("UK", authors.getCountry()));
            flag.flag();
        });
    }

    @Test
    void test_insert_returning_id(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Books table = catalog().PUBLIC.BOOKS;
        final InsertResultStep<BooksRecord> insert = executor.dsl()
                                                             .insertInto(table, table.ID, table.TITLE)
                                                             .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                             .returning(table.ID);
        executor.execute(insert, VertxReactiveDSL.instance().fetchMany(table, Collections.singletonList(table.ID)),
                         ar -> {
                             List<Record> records = assertRsSize(ctx, flag, ar, 1);
                             ctx.verify(() -> {
                                 final Record record = records.get(0);
                                 final BooksRecord into1 = record.into(BooksRecord.class);
                                 Assertions.assertEquals(8, into1.getId());
                                 Assertions.assertNull(into1.getTitle());
                                 final Books into2 = record.into(Books.class);
                                 Assertions.assertEquals(8, into2.getId());
                                 Assertions.assertNull(into2.getTitle());
                                 final Authors into3 = record.into(Authors.class);
                                 Assertions.assertEquals(8, into3.getId());
                                 Assertions.assertNull(into3.getCountry());
                             });

                             flag.flag();
                         });
    }

}
