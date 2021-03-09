package io.github.zero88.jooq.vertx.integtest.reactive;

import java.util.List;
import java.util.stream.Collectors;

import org.jooq.InsertResultStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record1;
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
import io.github.zero88.jooq.vertx.integtest.PostgreSQLHelper;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.records.AuthorsRecord;
import io.github.zero88.jooq.vertx.spi.PostgreSQLReactiveTest.AbstractPostgreSQLReactiveTest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class PgBatchInPoolTest extends AbstractPostgreSQLReactiveTest implements PostgreSQLHelper {

    @Override
    public boolean usePool() {
        return true;
    }

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt);
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
                              SelectListResultAdapter.vertxRecord(table, new ReactiveResultBatchConverter()), handler);
        executor.execute(executor.dsl().selectFrom(table), VertxReactiveDSL.instance().fetchVertxRecords(table),
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
        executor.batchExecute(insert, bindValues,
                              SelectListResultAdapter.create(table, new ReactiveResultBatchConverter(),
                                                             executor.dsl().newRecord(table.ID)), handler);
        executor.execute(executor.dsl().selectFrom(table), VertxReactiveDSL.instance().fetchVertxRecords(table),
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
        executor.batchExecute(insert, bindValues, ar -> {
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
        });
        executor.execute(executor.dsl().selectFrom(table), VertxReactiveDSL.instance().fetchVertxRecords(table),
                         ar -> assertRsSize(ctx, flag, ar, 10));
    }

}
