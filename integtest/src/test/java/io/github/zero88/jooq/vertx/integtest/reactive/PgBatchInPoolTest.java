package io.github.zero88.jooq.vertx.integtest.reactive;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jooq.InsertResultStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record1;
import org.jooq.exception.DataAccessException;
import org.jooq.exception.SQLStateClass;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooq.vertx.BatchResult;
import io.github.zero88.jooq.vertx.BatchReturningResult;
import io.github.zero88.jooq.vertx.BindBatchValues;
import io.github.zero88.jooq.vertx.JsonRecord;
import io.github.zero88.jooq.vertx.SqlErrorConverter;
import io.github.zero88.jooq.vertx.VertxReactiveDSL;
import io.github.zero88.jooq.vertx.integtest.PostgreSQLHelper;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.records.AuthorsRecord;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.records.BooksRecord;
import io.github.zero88.jooq.vertx.spi.PgErrorConverter;
import io.github.zero88.jooq.vertx.spi.PostgreSQLReactiveTest.AbstractPostgreSQLPoolTest;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class PgBatchInPoolTest extends AbstractPostgreSQLPoolTest implements PostgreSQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt);
    }

    @Override
    public SqlErrorConverter<? extends Throwable, ? extends RuntimeException> createErrorConverter() {
        return new PgErrorConverter();
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
        final Handler<AsyncResult<BatchReturningResult<JsonRecord<?>>>> handler = ar -> {
            try {
                if (ar.succeeded()) {
                    final BatchReturningResult<JsonRecord<?>> result = ar.result();
                    final List<JsonRecord<?>> records = result.getRecords();
                    ctx.verify(() -> {
                        String v = Json.CODEC.toString(
                            records.stream().map(JsonRecord::toJson).collect(Collectors.toList()));
                        Assertions.assertEquals(2, result.getTotal());
                        Assertions.assertEquals(2, result.getSuccesses());
                        Assertions.assertEquals("[{\"id\":9,\"name\":\"abc\",\"country\":\"AU\"},{\"id\":10," +
                                                "\"name\":\"haha\",\"country\":\"VN\"}]", v);
                    });
                    executor.execute(executor.dsl().selectFrom(table),
                                     VertxReactiveDSL.instance().fetchJsonRecords(table),
                                     ar2 -> assertRsSize(ctx, flag, ar2, 10));
                } else {
                    ctx.failNow(ar.cause());
                }
            } finally {
                flag.flag();
            }
        };
        executor.batch(insert, bindValues, VertxReactiveDSL.instance().batchJsonRecords(table), handler);
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
                    executor.execute(executor.dsl().selectFrom(table),
                                     VertxReactiveDSL.instance().fetchJsonRecords(table),
                                     ar2 -> assertRsSize(ctx, flag, ar2, 10));
                } else {
                    ctx.failNow(ar.cause());
                }
            } finally {
                flag.flag();
            }
        };
        executor.batch(insert, bindValues,
                       VertxReactiveDSL.instance().batch(table, executor.dsl().newRecord(table.ID)), handler);
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
        executor.batch(insert, bindValues, ar -> {
            try {
                if (ar.succeeded()) {
                    final BatchResult result = ar.result();
                    ctx.verify(() -> {
                        Assertions.assertEquals(2, result.getTotal());
                        Assertions.assertEquals(2, result.getSuccesses());
                    });
                    executor.execute(executor.dsl().selectFrom(table),
                                     VertxReactiveDSL.instance().fetchJsonRecords(table),
                                     ar2 -> assertRsSize(ctx, flag, ar2, 10));
                } else {
                    ctx.failNow(ar.cause());
                }
            } finally {
                flag.flag();
            }
        });
    }

    @Test
    void transaction_success(VertxTestContext context) {
        final Checkpoint flag = context.checkpoint(2);
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Books table = catalog().PUBLIC.BOOKS;
        executor.transaction().run(tx -> {
            final InsertResultStep<BooksRecord> q1 = tx.dsl()
                                                       .insertInto(table, table.ID, table.TITLE)
                                                       .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                       .returning(table.ID);
            final InsertResultStep<BooksRecord> q2 = tx.dsl()
                                                       .insertInto(table, table.ID, table.TITLE)
                                                       .values(Arrays.asList(DSL.defaultValue(table.ID), "xyz"))
                                                       .returning(table.ID);
            return tx.execute(q1, VertxReactiveDSL.instance().fetchOne(table))
                     .flatMap(b1 -> tx.execute(q2, VertxReactiveDSL.instance().fetchOne(table)));
        }, ar -> {
            context.verify(() -> {
                Assertions.assertTrue(ar.succeeded());
                executor.execute(executor.dsl().selectFrom(table), VertxReactiveDSL.instance().fetchMany(table),
                                 ar2 -> assertRsSize(context, flag, ar2, 9));
            });
            flag.flag();
        });
    }

    @Test
    void transaction_failed_due_to_conflict_key(VertxTestContext context) {
        final Checkpoint flag = context.checkpoint(2);
        final io.github.zero88.jooq.vertx.integtest.pgsql.tables.Books table = catalog().PUBLIC.BOOKS;
        executor.transaction().run(tx -> {
            final InsertResultStep<BooksRecord> q1 = tx.dsl()
                                                       .insertInto(table, table.ID, table.TITLE)
                                                       .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                       .returning(table.ID);
            final InsertResultStep<BooksRecord> q2 = tx.dsl()
                                                       .insertInto(table, table.ID, table.TITLE)
                                                       .values(1, "xyz")
                                                       .returning(table.ID);
            return tx.execute(q1, VertxReactiveDSL.instance().fetchOne(table))
                     .flatMap(b1 -> tx.execute(q2, VertxReactiveDSL.instance().fetchOne(table)));
        }, ar -> {
            context.verify(() -> {
                Assertions.assertTrue(ar.failed());
                Assertions.assertTrue(ar.cause() instanceof DataAccessException);
                Assertions.assertEquals(SQLStateClass.C23_INTEGRITY_CONSTRAINT_VIOLATION,
                                        ((DataAccessException) ar.cause()).sqlStateClass());
                executor.execute(executor.dsl().selectFrom(table), VertxReactiveDSL.instance().fetchMany(table),
                                 ar2 -> assertRsSize(context, flag, ar2, 7));
            });
            flag.flag();
        });
    }

}
