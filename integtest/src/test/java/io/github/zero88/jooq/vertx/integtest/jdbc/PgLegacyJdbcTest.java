package io.github.zero88.jooq.vertx.integtest.jdbc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jooq.InsertResultStep;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooq.vertx.BaseVertxLegacyJdbcSql;
import io.github.zero88.jooq.vertx.PostgreSQLTest.PostgreSQLJdbcTest;
import io.github.zero88.jooq.vertx.VertxLegacyJdbcExecutor;
import io.github.zero88.jooq.vertx.converter.LegacyRowRecordConverter;
import io.github.zero88.jooq.vertx.converter.SqlRowBatchConverter;
import io.github.zero88.jooq.vertx.converter.SqlRowRecordConverter;
import io.github.zero88.jooq.vertx.integtest.PostgreSQLHelper;
import io.github.zero88.jooq.vertx.integtest.pgsql.DefaultCatalog;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.Books;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.records.BooksRecord;
import io.github.zero88.jooq.vertx.record.VertxJooqRecord;
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
        executor.execute(executor.dsl().selectFrom(table), new LegacyRowRecordConverter<>(table),
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
        executor.execute(insert, new LegacyRowRecordConverter<>(table), ar -> {
            final List<VertxJooqRecord<?>> records = assertRsSize(ctx, flag, ar, 1);
            ctx.verify(() -> Assertions.assertEquals(new JsonObject().put("id", 8).put("title", null),
                                                     records.get(0).toJson()));
            flag.flag();
        });
    }

    @Test
    @Disabled
    void test_batch_insert(Vertx vertx, VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final VertxLegacyJdbcExecutor executor = VertxLegacyJdbcExecutor.builder()
                                                                        .vertx(vertx)
                                                                        .dsl(dsl(dialect()))
                                                                        .sqlClient(sqlClient())
                                                                        .build();

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
        final SqlRowBatchConverter<Books> converter = new SqlRowBatchConverter<>(table);
        executor.batchExecute(insert, ar -> {
            try {
                if (ar.succeeded()) {
                    //                    final List<BooksRecord> records = converter.record(ar.result());
                    //                    ctx.verify(() -> {
                    //                        final List<io.github.zero88.qwe.sql.jooq.integtest.s3.tables.pojos
                    //                        .Books> list
                    //                            = records.stream().map(r -> {
                    //                            final io.github.zero88.qwe.sql.jooq.integtest.s3.tables.pojos.Books
                    //                            books
                    //                                = new io.github.zero88.qwe.sql.jooq.integtest.s3.tables.pojos
                    //                                .Books();
                    //                            books.from(r);
                    //                            return books;
                    //                        }).collect(Collectors.toList());
                    //                        System.out.println(JsonData.MAPPER.writeValueAsString(list));
                    //                        Assertions.assertEquals(2, records.size());
                    //                    });
                } else {
                    ctx.failNow(ar.cause());
                }
            } finally {
                flag.flag();
            }
        });
        final SqlRowRecordConverter<Books> converter2 = new SqlRowRecordConverter<>(table);
        //        executor.execute(executor.dsl().selectFrom(table), ar -> {
        //            try {
        //                if (ar.succeeded()) {
        //                    final List<BooksRecord> records = converter2.convert(ar.result());
        //                    System.out.println(records);
        //                    ctx.verify(() -> Assertions.assertEquals(records.size(), 2));
        //                } else {
        //                    ctx.failNow(ar.cause());
        //                }
        //            } finally {
        //                flag.flag();
        //            }
        //        });
    }

}
