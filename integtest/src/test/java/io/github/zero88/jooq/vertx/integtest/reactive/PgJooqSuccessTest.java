package io.github.zero88.jooq.vertx.integtest.reactive;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.InsertResultStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectConditionStep;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooq.vertx.BatchResult;
import io.github.zero88.jooq.vertx.BatchReturningResult;
import io.github.zero88.jooq.vertx.BindBatchValues;
import io.github.zero88.jooq.vertx.VertxJooqRecord;
import io.github.zero88.jooq.vertx.adapter.ListResultAdapter;
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
        executor.execute(query, ListResultAdapter.createVertxRecord(table, new ReactiveResultSetConverter()),
                         ar -> assertRsSize(ctx, flag, ar, 7));
    }

    @Test
    void test_query_convert_by_record_class(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        final SelectWhereStep<AuthorsRecord> query = executor.dsl().selectFrom(table);
        executor.execute(query, ListResultAdapter.create(table, new ReactiveResultSetConverter(), Authors.class),
                         ar -> {
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
        executor.execute(query, ListResultAdapter.create(table, new ReactiveResultSetConverter(), table), ar -> {
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
        executor.execute(insert, ListResultAdapter.create(table, new ReactiveResultSetConverter(),
                                                          Collections.singletonList(table.ID)), ar -> {
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

    @Test
    void test_batch_insert_and_returning_all(VertxTestContext ctx) {
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
        final Handler<AsyncResult<BatchReturningResult<VertxJooqRecord<?>>>> handler = ar -> {
            try {
                if (ar.succeeded()) {
                    final BatchReturningResult<VertxJooqRecord<?>> result = ar.result();
                    final List<VertxJooqRecord<?>> records = result.getRecords();
                    ctx.verify(() -> {
                        String v = Json.CODEC.toString(
                            records.stream().map(VertxJooqRecord::toJson).collect(Collectors.toList()));
                        Assertions.assertEquals(2, result.getTotal());
                        Assertions.assertEquals(2, result.getSuccesses());
                        Assertions.assertEquals("[{\"id\":9,\"name\":\"abc\",\"country\":\"AU\"},{\"id\":10," +
                                                "\"name\":\"haha\",\"country\":\"VN\"}]", v);
                    });
                } else {
                    ctx.failNow(ar.cause());
                }
            } finally {
                flag.flag();
            }
        };
        executor.batchExecute(insert, bindValues,
                              ListResultAdapter.createVertxRecord(table, new ReactiveResultBatchConverter()), handler);
        executor.execute(executor.dsl().selectFrom(table),
                         ListResultAdapter.createVertxRecord(table, new ReactiveResultSetConverter()),
                         ar -> assertRsSize(ctx, flag, ar, 10));
    }

    @Test
    void test_batch_insert_and_returning_id(VertxTestContext ctx) {
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
                                                               .returning(table.ID);
        final Handler<AsyncResult<BatchReturningResult<Record1<?>>>> handler = ar -> {
            try {
                if (ar.succeeded()) {
                    final BatchReturningResult<Record1<?>> result = ar.result();
                    final List<Record1<?>> records = result.getRecords();
                    ctx.verify(() -> {
                        Assertions.assertEquals(2, result.getTotal());
                        Assertions.assertEquals(2, result.getSuccesses());
                        Assertions.assertEquals(9, result.getRecords().get(0).value1());
                        Assertions.assertEquals(10, result.getRecords().get(1).value1());
                        System.out.println(records);
                    });
                } else {
                    ctx.failNow(ar.cause());
                }
            } finally {
                flag.flag();
            }
        };
        executor.batchExecute(insert, bindValues, ListResultAdapter.create(table, new ReactiveResultBatchConverter(),
                                                                           executor.dsl().newRecord(table.ID)),
                              handler);
        executor.execute(executor.dsl().selectFrom(table),
                         ListResultAdapter.createVertxRecord(table, new ReactiveResultSetConverter()),
                         ar -> assertRsSize(ctx, flag, ar, 10));
    }

    @Test
    void test_batch_insert_and_returning_count(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Authors table = catalog().PUBLIC.AUTHORS;
        AuthorsRecord rec1 = new AuthorsRecord().setName("abc").setCountry("AU");
        AuthorsRecord rec2 = new AuthorsRecord().setName("haha");
        final BindBatchValues bindValues = new BindBatchValues().register(table.NAME)
                                                                .register(table.COUNTRY, "VN")
                                                                .add(rec1, rec2);
        final InsertSetMoreStep<AuthorsRecord> insert = executor.dsl()
                                                                .insertInto(table)
                                                                .set(bindValues.getDummyValues());
        final Handler<AsyncResult<BatchResult>> handler = ar -> {
            try {
                if (ar.succeeded()) {
                    final BatchResult result = ar.result();
                    ctx.verify(() -> {
                        Assertions.assertEquals(2, result.getTotal());
                        Assertions.assertEquals(2, result.getSuccesses());
                    });
                } else {
                    ctx.failNow(ar.cause());
                }
            } finally {
                flag.flag();
            }
        };
        executor.batchExecute(insert, bindValues, handler);
        executor.execute(executor.dsl().selectFrom(table),
                         ListResultAdapter.createVertxRecord(table, new ReactiveResultSetConverter()),
                         ar -> assertRsSize(ctx, flag, ar, 10));
    }

}
