package io.github.zero88.integtest.jooqx.pg.jooq;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.InsertResultStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record1;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.BatchResult;
import io.github.zero88.jooqx.BatchReturningResult;
import io.github.zero88.jooqx.BindBatchValues;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JsonRecord;
import io.github.zero88.jooqx.spi.pg.PgPoolProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.sample.model.pgsql.tables.Authors;
import io.github.zero88.sample.model.pgsql.tables.records.AuthorsRecord;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;

class PgPoolBatchTest extends PgSQLJooqxTest<PgPool>
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
        final Handler<AsyncResult<BatchReturningResult<JsonRecord<AuthorsRecord>>>> asserter = ar -> ctx.verify(() -> {
            final BatchReturningResult<JsonRecord<AuthorsRecord>> result = assertSuccess(ctx, ar);
            final List<JsonRecord<AuthorsRecord>> records = result.getRecords();
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
        jooqx.batchResult(insert, bindValues, DSLAdapter.fetchJsonRecords(table), asserter);
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
            System.out.println(records);
            Assertions.assertEquals(2, result.getTotal());
            Assertions.assertEquals(2, result.getSuccesses());
            Assertions.assertEquals(9, records.get(0).value1());
            Assertions.assertEquals(10, records.get(1).value1());
            jooqx.execute(dsl -> dsl.selectFrom(table), DSLAdapter.fetchJsonRecords(table), ar2 -> {
                assertResultSize(ctx, ar2, 10);
                flag.flag();
            });
        });
        jooqx.batchResult(insert, bindValues, DSLAdapter.fetchMany(jooqx.dsl().newRecord(table.ID)), asserter);
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

}
