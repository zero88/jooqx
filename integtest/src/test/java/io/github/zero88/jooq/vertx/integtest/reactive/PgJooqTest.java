package io.github.zero88.jooq.vertx.integtest.reactive;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.InsertResultStep;
import org.jooq.SelectConditionStep;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooq.vertx.BaseVertxReactiveSql;
import io.github.zero88.jooq.vertx.PostgreSQLTest.PostgreSQLReactiveTest;
import io.github.zero88.jooq.vertx.converter.BindBatchValues;
import io.github.zero88.jooq.vertx.converter.ReactiveResultBatchConverter;
import io.github.zero88.jooq.vertx.converter.ReactiveResultSetConverter;
import io.github.zero88.jooq.vertx.integtest.PostgreSQLHelper;
import io.github.zero88.jooq.vertx.integtest.pgsql.DefaultCatalog;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.pojos.Authors;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.pojos.Books;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.records.AuthorsRecord;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.records.BooksRecord;
import io.github.zero88.jooq.vertx.record.VertxJooqRecord;
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
class PgJooqTest extends BaseVertxReactiveSql<DefaultCatalog> implements PostgreSQLReactiveTest, PostgreSQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, server);
    }

    @Test
    void test_query(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Books table = catalog().PUBLIC.BOOKS;
        final SelectWhereStep<BooksRecord> query = executor.dsl().selectFrom(table);
        executor.execute(query, new ReactiveResultSetConverter<>(table), ar -> assertRsSize(ctx, flag, ar, 7));
    }

    @Test
    void test_query_convert_by_record_class(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        final SelectWhereStep<AuthorsRecord> query = executor.dsl().selectFrom(table);
        executor.execute(query, new ReactiveResultSetConverter<>(table), Authors.class, ar -> {
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
        executor.execute(query, new ReactiveResultSetConverter<>(table), table, ar -> {
            final List<AuthorsRecord> books = assertRsSize(ctx, flag, ar, 1);
            final AuthorsRecord authors = books.get(0);
            ctx.verify(() -> Assertions.assertEquals(3, authors.getId()));
            ctx.verify(() -> Assertions.assertEquals("Jane Austen", authors.getName()));
            ctx.verify(() -> Assertions.assertEquals("UK", authors.getCountry()));
            flag.flag();
        });
    }

    @Test
    void test_insert(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Books table = catalog().PUBLIC.BOOKS;
        final InsertResultStep<BooksRecord> insert = executor.dsl()
                                                             .insertInto(table, table.ID, table.TITLE)
                                                             .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                             .returning(table.ID);
        executor.execute(insert, new ReactiveResultSetConverter<>(table), ar -> {
            List<VertxJooqRecord<?>> records = assertRsSize(ctx, flag, ar, 1);
            ctx.verify(() -> {
                final VertxJooqRecord<?> record = records.get(0);
                Assertions.assertEquals(new JsonObject().put("id", 8).put("title", null), record.toJson());
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

    @Test
    void test_batch_insert(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        AuthorsRecord rec1 = new AuthorsRecord().setName("abc").setCountry("AU");
        AuthorsRecord rec2 = new AuthorsRecord().setName("haha");

        final BindBatchValues bindValues = new BindBatchValues().register(table.NAME)
                                                                .register(table.COUNTRY, "VN")
                                                                .add(rec1, rec2);
        final InsertResultStep<AuthorsRecord> insert = executor.dsl()
                                                               .insertInto(table)
                                                               .set(bindValues.getDummyValues())
                                                               .returning();
        final Handler<AsyncResult<List<VertxJooqRecord<?>>>> handler = ar -> {
            try {
                if (ar.succeeded()) {
                    final List<VertxJooqRecord<?>> records = ar.result();
                    ctx.verify(() -> {
                        String v = Json.CODEC.toString(
                            records.stream().map(VertxJooqRecord::toJson).collect(Collectors.toList()));
                        Assertions.assertEquals("[{\"id\":9,\"name\":\"abc\",\"country\":\"AU\"},{\"id\":10," +
                                                "\"name\":\"haha\",\"country\":\"VN\"}]", v);
                        Assertions.assertEquals(2, records.size());
                    });
                } else {
                    ctx.failNow(ar.cause());
                }
            } finally {
                flag.flag();
            }
        };
        executor.batchExecute(insert, new ReactiveResultBatchConverter<>(table), bindValues, handler);
        executor.execute(executor.dsl().selectFrom(table), new ReactiveResultSetConverter<>(table),
                         ar -> assertRsSize(ctx, flag, ar, 10));
    }

}
