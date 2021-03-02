package io.github.zero88.jooq.vertx.integtest.reactive;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jooq.InsertResultStep;
import org.jooq.SelectWhereStep;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooq.vertx.BaseVertxReactiveSql;
import io.github.zero88.jooq.vertx.PostgreSQLTest.PostgreSQLReactiveTest;
import io.github.zero88.jooq.vertx.converter.SqlRowBatchConverter;
import io.github.zero88.jooq.vertx.converter.SqlRowRecordConverter;
import io.github.zero88.jooq.vertx.integtest.PostgreSQLHelper;
import io.github.zero88.jooq.vertx.integtest.pgsql.DefaultCatalog;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.Books;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.records.BooksRecord;
import io.github.zero88.jooq.vertx.record.VertxJooqRecord;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Tuple;

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
        final Books table = catalog().PUBLIC.BOOKS;
        final SelectWhereStep<BooksRecord> query = executor.dsl().selectFrom(table);
        executor.execute(query, new SqlRowRecordConverter<>(table), ar -> {
            List<VertxJooqRecord<?>> records = assertRsSize(ctx, flag, ar, 7);
            System.out.println(records);
        });
    }

    @Test
    void test_insert(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final Books table = catalog().PUBLIC.BOOKS;
        final InsertResultStep<BooksRecord> insert = executor.dsl()
                                                             .insertInto(table, table.ID, table.TITLE)
                                                             .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                             .returning(table.ID);
        executor.execute(insert, new SqlRowRecordConverter<>(table), ar -> {
            List<VertxJooqRecord<?>> records = assertRsSize(ctx, flag, ar, 1);
            ctx.verify(() -> Assertions.assertEquals(new JsonObject().put("id", 8).put("title", null),
                                                     records.get(0).toJson()));
            flag.flag();
        });
    }

    @Test
    void test_batch_insert(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final Books table = catalog().PUBLIC.BOOKS;
        BooksRecord rec1 = new BooksRecord().setTitle("abc");
        BooksRecord rec2 = new BooksRecord().setTitle("haha");

        final TableField<BooksRecord, Integer> identityField = table.getIdentity().getField();
        final Object[] objects = table.fieldStream()
                                      .map(f -> identityField.equals(f) ? DSL.defaultValue() : null)
                                      .toArray();
        final InsertResultStep<BooksRecord> insert = executor.dsl()
                                                             .insertInto(table, table.fields())
                                                             .values(objects)
                                                             .returning();
        final String sql = executor.helper().toPreparedQuery(executor.dsl().configuration(), insert);
        final List<Object> r1Values = IntStream.range(1, rec1.size()).mapToObj(rec1::get).collect(Collectors.toList());
        final List<Object> r2Values = IntStream.range(1, rec2.size()).mapToObj(rec2::get).collect(Collectors.toList());
        sqlClient().preparedQuery(sql).executeBatch(Arrays.asList(Tuple.from(r1Values), Tuple.from(r2Values)), ar -> {
            try {
                if (ar.succeeded()) {
                    final List<VertxJooqRecord<?>> records = new SqlRowBatchConverter<>(table).convert(ar.result());
                    ctx.verify(() -> {
                        System.out.println(Json.CODEC.toString(
                            records.stream().map(VertxJooqRecord::toJson).collect(Collectors.toList())));
                        Assertions.assertEquals(2, records.size());
                    });
                } else {
                    ctx.failNow(ar.cause());
                }
            } finally {
                flag.flag();
            }
        });
        executor.execute(executor.dsl().selectFrom(table), new SqlRowRecordConverter<>(table),
                         ar -> assertRsSize(ctx, flag, ar, 9));
    }

    /**
     * <a href="vertx-sql-client#909">https://github.com/eclipse-vertx/vertx-sql-client/issues/909</a>
     */
    @Test
    @Disabled
    void bug_vertx_sql_909(Vertx vertx, VertxTestContext ctx) {

    }

}
