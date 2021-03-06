package io.github.zero88.jooq.vertx.integtest.jdbc;

import java.util.Arrays;
import java.util.List;

import org.jooq.InsertResultStep;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooq.vertx.BaseVertxLegacyJdbcSql;
import io.github.zero88.jooq.vertx.BindBatchValues;
import io.github.zero88.jooq.vertx.PostgreSQLTest.PostgreSQLJdbcTest;
import io.github.zero88.jooq.vertx.VertxJooqRecord;
import io.github.zero88.jooq.vertx.VertxLegacyJdbcExecutor;
import io.github.zero88.jooq.vertx.adapter.ListResultAdapter;
import io.github.zero88.jooq.vertx.converter.LegacyResultSetConverter;
import io.github.zero88.jooq.vertx.integtest.PostgreSQLHelper;
import io.github.zero88.jooq.vertx.integtest.pgsql.DefaultCatalog;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.Books;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.records.BooksRecord;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class PgLegacyJdbcTest extends BaseVertxLegacyJdbcSql<DefaultCatalog> implements PostgreSQLJdbcTest, PostgreSQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, server);
    }

    @Test
    void test_query(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Books table = catalog().PUBLIC.BOOKS;
        executor.execute(executor.dsl().selectFrom(table),
                         ListResultAdapter.create(table, new LegacyResultSetConverter(), table),
                         ar -> assertRsSize(ctx, flag, ar, 7));
    }

    @Test
    void test_insert(Vertx vertx, VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final VertxLegacyJdbcExecutor executor = VertxLegacyJdbcExecutor.builder()
                                                                        .vertx(vertx)
                                                                        .dsl(dsl(dialect()))
                                                                        .sqlClient(sqlClient())
                                                                        .build();
        final Books table = catalog().PUBLIC.BOOKS;
        final InsertResultStep<BooksRecord> insert = executor.dsl()
                                                             .insertInto(table, table.ID, table.TITLE)
                                                             .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                             .returning(table.ID);
        executor.execute(insert, ListResultAdapter.createVertxRecord(table, new LegacyResultSetConverter()), ar -> {
            final List<VertxJooqRecord<?>> records = assertRsSize(ctx, flag, ar, 1);
            ctx.verify(() -> Assertions.assertEquals(new JsonObject().put("id", 8).put("title", null),
                                                     records.get(0).toJson()));
            flag.flag();
        });
    }

    @Test
    void test_batch_insert(Vertx vertx, VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final VertxLegacyJdbcExecutor executor = VertxLegacyJdbcExecutor.builder()
                                                                        .vertx(vertx)
                                                                        .dsl(dsl(dialect()))
                                                                        .sqlClient(sqlClient())
                                                                        .build();

        final Books table = catalog().PUBLIC.BOOKS;
        BooksRecord rec1 = new BooksRecord().setTitle("abc");
        BooksRecord rec2 = new BooksRecord().setTitle("xyz");
        BooksRecord rec3 = new BooksRecord().setTitle("qwe");

        final BindBatchValues bindValues = new BindBatchValues().register(table.TITLE).add(rec1, rec2, rec3);
        final InsertResultStep<BooksRecord> insert = executor.dsl()
                                                             .insertInto(table)
                                                             .set(bindValues.getDummyValues())
                                                             .returning();
        executor.batchExecute(insert, bindValues, ar -> {
            if (ar.succeeded()) {
                ctx.verify(() -> Assertions.assertEquals(3, ar.result().getSuccesses()));
                flag.flag();
            }
        });
        executor.execute(executor.dsl().selectFrom(table),
                         ListResultAdapter.createVertxRecord(table, new LegacyResultSetConverter()), ar -> {
                if (ar.succeeded()) {
                    final List<VertxJooqRecord<?>> records = assertRsSize(ctx, flag, ar, 10);
                    ctx.verify(() -> {
                        Assertions.assertEquals(new JsonObject().put("id", 8).put("title", "abc"),
                                                records.get(7).toJson());
                        Assertions.assertEquals(new JsonObject().put("id", 9).put("title", "xyz"),
                                                records.get(8).toJson());
                        Assertions.assertEquals(new JsonObject().put("id", 10).put("title", "qwe"),
                                                records.get(8).toJson());
                    });
                    flag.flag();
                }
            });
    }

}
