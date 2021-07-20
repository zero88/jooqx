package io.zero88.jooqx.integtest.spi.pg.jooq;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.InsertResultStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record1;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.zero88.jooqx.BatchResult;
import io.zero88.jooqx.BatchReturningResult;
import io.zero88.jooqx.BindBatchValues;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.jooqx.JsonRecord;
import io.zero88.jooqx.integtest.pgsql.tables.Authors;
import io.zero88.jooqx.integtest.pgsql.tables.Books;
import io.zero88.jooqx.integtest.pgsql.tables.records.AuthorsRecord;
import io.zero88.jooqx.integtest.pgsql.tables.records.BooksRecord;
import io.zero88.jooqx.integtest.spi.pg.PostgreSQLHelper.PgUseJooqType;
import io.zero88.jooqx.spi.pg.PgPoolProvider;
import io.zero88.jooqx.spi.pg.PgSQLReactiveTest;
import io.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;

class PgReABatchTest extends PgSQLReactiveTest<PgPool>
    implements PgPoolProvider, PgUseJooqType, PgSQLErrorConverterProvider {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql");
    }

    @Test
    void test_batch_insert_and_returning_all(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Authors table = schema().AUTHORS;
        AuthorsRecord rec1 = new AuthorsRecord().setName("abc").setCountry("AU");
        AuthorsRecord rec2 = new AuthorsRecord().setName("haha");

        final BindBatchValues bindValues = new BindBatchValues().register(table.NAME)
                                                                .registerValue(table.COUNTRY, "VN")
                                                                .add(rec1, rec2);
        final InsertResultStep<AuthorsRecord> insert = jooqx.dsl()
                                                            .insertInto(table)
                                                            .set(bindValues.getDummyValues())
                                                            .returning();
        final Handler<AsyncResult<BatchReturningResult<JsonRecord<?>>>> asserter = ar -> ctx.verify(() -> {
            final BatchReturningResult<JsonRecord<?>> result = assertSuccess(ctx, ar);
            final List<JsonRecord<?>> records = result.getRecords();
            String v = Json.CODEC.toString(records.stream().map(JsonRecord::toJson).collect(Collectors.toList()));
            Assertions.assertEquals(2, result.getTotal());
            Assertions.assertEquals(2, result.getSuccesses());
            Assertions.assertEquals("[{\"id\":9,\"name\":\"abc\",\"country\":\"AU\"},{\"id\":10," +
                                    "\"name\":\"haha\",\"country\":\"VN\"}]", v);
            jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchJsonRecords(table), ar2 -> {
                assertResultSize(ctx, ar2, 10);
                flag.flag();
            });
        });
        jooqx.batch(insert, bindValues, DSLAdapter.fetchJsonRecords(table), asserter);
    }

    @Test
    void test_batch_insert_and_returning_id(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Authors table = schema().AUTHORS;
        AuthorsRecord rec1 = new AuthorsRecord().setName("abc").setCountry("AU");
        AuthorsRecord rec2 = new AuthorsRecord().setName("haha");
        final BindBatchValues bindValues = new BindBatchValues().register(table.NAME)
                                                                .registerValue(table.COUNTRY, "VN")
                                                                .add(rec1, rec2);
        final InsertResultStep<AuthorsRecord> insert = jooqx.dsl()
                                                            .insertInto(table)
                                                            .set(bindValues.getDummyValues())
                                                            .returning(table.ID);
        final Handler<AsyncResult<BatchReturningResult<Record1<?>>>> asserter = ar -> ctx.verify(() -> {
            final BatchReturningResult<Record1<?>> result = assertSuccess(ctx, ar);
            final List<Record1<?>> records = result.getRecords();
            Assertions.assertEquals(2, result.getTotal());
            Assertions.assertEquals(2, result.getSuccesses());
            Assertions.assertEquals(9, result.getRecords().get(0).value1());
            Assertions.assertEquals(10, result.getRecords().get(1).value1());
            System.out.println(records);
            jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchJsonRecords(table), ar2 -> {
                assertResultSize(ctx, ar2, 10);
                flag.flag();
            });
        });
        final Record1<Integer> t1Record1 = jooqx.dsl().newRecord(table.ID);
        jooqx.batch(insert, bindValues, DSLAdapter.fetchMany(table, t1Record1), asserter);
    }

    @Test
    void test_batch_insert_and_returning_count(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Authors table = schema().AUTHORS;
        AuthorsRecord rec1 = new AuthorsRecord().setName("abc").setCountry("AU");
        AuthorsRecord rec2 = new AuthorsRecord().setName("haha");
        final BindBatchValues bindValues = new BindBatchValues().register(table.NAME)
                                                                .registerValue(table.COUNTRY, "VN")
                                                                .add(rec1, rec2);
        final InsertSetMoreStep<AuthorsRecord> insert = jooqx.dsl().insertInto(table).set(bindValues.getDummyValues());
        jooqx.batch(insert, bindValues, ar -> ctx.verify(() -> {
            final BatchResult result = assertSuccess(ctx, ar);
            Assertions.assertEquals(2, result.getTotal());
            Assertions.assertEquals(2, result.getSuccesses());
            jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchJsonRecords(table), ar2 -> {
                assertResultSize(ctx, ar2, 10);
                flag.flag();
            });
        }));
    }

    @Test
    void transaction_insert_success(VertxTestContext context) {
        final Checkpoint flag = context.checkpoint();
        final Books table = schema().BOOKS;
        jooqx.transaction().run(tx -> {
            final InsertResultStep<BooksRecord> q1 = tx.dsl()
                                                       .insertInto(table, table.ID, table.TITLE)
                                                       .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                       .returning(table.ID);
            final InsertResultStep<BooksRecord> q2 = tx.dsl()
                                                       .insertInto(table, table.ID, table.TITLE)
                                                       .values(Arrays.asList(DSL.defaultValue(table.ID), "xyz"))
                                                       .returning(table.ID);
            return tx.execute(q1, DSLAdapter.fetchOne(table)).flatMap(b1 -> tx.execute(q2, DSLAdapter.fetchOne(table)));
        }, ar -> context.verify(() -> {
            final BooksRecord record = assertSuccess(context, ar);
            Assertions.assertNotNull(record);
            Assertions.assertEquals(9, record.getId());
            jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchMany(table), ar2 -> {
                assertResultSize(context, ar2, 9);
                flag.flag();
            });
        }));
    }

    @Test
    void transaction_failed_due_to_conflict_key(VertxTestContext context) {
        final Checkpoint flag = context.checkpoint();
        final Books table = schema().BOOKS;
        jooqx.transaction().run(tx -> {
            final InsertResultStep<BooksRecord> q1 = tx.dsl()
                                                       .insertInto(table, table.ID, table.TITLE)
                                                       .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                       .returning(table.ID);
            final InsertResultStep<BooksRecord> q2 = tx.dsl()
                                                       .insertInto(table, table.ID, table.TITLE)
                                                       .values(1, "xyz")
                                                       .returning(table.ID);
            return tx.execute(q1, DSLAdapter.fetchOne(table)).flatMap(b1 -> tx.execute(q2, DSLAdapter.fetchOne(table)));
        }, ar -> context.verify(() -> {
            assertJooqException(context, ar, SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION);
            jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchMany(table), ar2 -> {
                assertResultSize(context, ar2, 7);
                flag.flag();
            });
        }));
    }

    @Test
    void transaction_batch_success(VertxTestContext context) {
        final Checkpoint flag = context.checkpoint();
        final Authors table = schema().AUTHORS;
        jooqx.transaction().run(tx -> {
            AuthorsRecord rec1 = new AuthorsRecord().setName("abc").setCountry("AU");
            AuthorsRecord rec2 = new AuthorsRecord().setName("haha");
            final BindBatchValues bindValues = new BindBatchValues().register(table.NAME)
                                                                    .registerValue(table.COUNTRY, "VN")
                                                                    .add(rec1, rec2);
            final InsertSetMoreStep<AuthorsRecord> insert = jooqx.dsl()
                                                                 .insertInto(table)
                                                                 .set(bindValues.getDummyValues());
            return tx.batch(insert, bindValues);
        }, ar -> context.verify(() -> {
            final BatchResult result = assertSuccess(context, ar);
            Assertions.assertNotNull(result);
            Assertions.assertEquals(2, result.getSuccesses());
            Assertions.assertEquals(2, result.getTotal());
            jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchMany(table), ar2 -> {
                assertResultSize(context, ar2, 10);
                flag.flag();
            });
        }));
    }

}
