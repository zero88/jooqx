package io.github.zero88.jooq.vertx.integtest.reactive;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jooq.InsertResultStep;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooq.vertx.BaseVertxReactiveSql;
import io.github.zero88.jooq.vertx.JooqSql;
import io.github.zero88.jooq.vertx.PostgreSQLTest.PostgreSQLReactiveTest;
import io.github.zero88.jooq.vertx.VertxReactiveSqlExecutor;
import io.github.zero88.jooq.vertx.converter.BatchRowSetConverter;
import io.github.zero88.jooq.vertx.converter.RowSetConverter;
import io.github.zero88.jooq.vertx.integtest.PostgreSQLHelper;
import io.github.zero88.jooq.vertx.integtest.pgsql.DefaultCatalog;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.Books;
import io.github.zero88.jooq.vertx.integtest.pgsql.tables.records.BooksRecord;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Tuple;

class PgJooqTest extends BaseVertxReactiveSql implements JooqSql<DefaultCatalog>, PostgreSQLReactiveTest {

    @Override
    public DefaultCatalog catalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        PostgreSQLHelper.prepareDatabase(ctx, this, server);
    }

    @Test
    void test_query(Vertx vertx, VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final VertxReactiveSqlExecutor executor = VertxReactiveSqlExecutor.builder()
                                                                          .vertx(vertx)
                                                                          .dsl(dsl(dialect()))
                                                                          .sqlClient(sqlClient())
                                                                          .build();
        final Books table = catalog().PUBLIC.BOOKS;
        final RowSetConverter<BooksRecord, Books> converter2 = new RowSetConverter<>(table, BooksRecord.class);
        executor.execute(executor.dsl().selectFrom(table), ar -> {
            try {
                if (ar.succeeded()) {
                    final List<BooksRecord> records = converter2.convert(ar.result());
                    System.out.println(records);
                    ctx.verify(() -> Assertions.assertEquals(records.size(), 7));
                } else {
                    ctx.failNow(ar.cause());
                }
            } finally {
                flag.flag();
            }
        });
    }

    @Test
    void test_insert(Vertx vertx, VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final VertxReactiveSqlExecutor executor = VertxReactiveSqlExecutor.builder()
                                                                          .vertx(vertx)
                                                                          .dsl(dsl(dialect()))
                                                                          .sqlClient(sqlClient())
                                                                          .build();
        final Books table = catalog().PUBLIC.BOOKS;
        //        final BooksRecord rec1 = new BooksRecord().setTitle("abc");
        final InsertResultStep<BooksRecord> insert = executor.dsl()
                                                             .insertInto(table, table.ID, table.TITLE)
                                                             .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                             .returning();
        executor.execute(insert, ar -> {
            try {
                if (ar.succeeded()) {
                    final RowSetConverter<BooksRecord, Books> converter2 = new RowSetConverter<>(table,
                                                                                                 BooksRecord.class);
                    final List<BooksRecord> records = converter2.convert(ar.result());
                    System.out.println("Got " + records.size() + " rows");
                    ctx.verify(() -> Assertions.assertTrue(records.size() > 0));
                } else {
                    ctx.failNow(ar.cause());
                }
            } finally {
                flag.flag();
            }
        });
    }

    @Test
    void test_batch_insert(Vertx vertx, VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint(2);
        final VertxReactiveSqlExecutor executor = VertxReactiveSqlExecutor.builder()
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
        final BatchRowSetConverter<BooksRecord, Books> converter = new BatchRowSetConverter<>(table, BooksRecord.class);
        sqlClient().preparedQuery(sql).executeBatch(Arrays.asList(Tuple.from(r1Values), Tuple.from(r2Values)), ar -> {
            try {
                if (ar.succeeded()) {
                    final List<BooksRecord> records = converter.convert(ar.result());
                    ctx.verify(() -> {
                        final List<io.github.zero88.jooq.vertx.integtest.pgsql.tables.pojos.Books> list
                            = records.stream().map(r -> {
                            final io.github.zero88.jooq.vertx.integtest.pgsql.tables.pojos.Books books
                                = new io.github.zero88.jooq.vertx.integtest.pgsql.tables.pojos.Books();
                            books.from(r);
                            return books;
                        }).collect(Collectors.toList());
                        System.out.println(Json.CODEC.toString(list));
                        Assertions.assertEquals(2, records.size());
                    });
                } else {
                    ctx.failNow(ar.cause());
                }
            } finally {
                flag.flag();
            }
        });
        final RowSetConverter<BooksRecord, Books> converter2 = new RowSetConverter<>(table, BooksRecord.class);
        executor.execute(executor.dsl().selectFrom(table), ar -> {
            try {
                if (ar.succeeded()) {
                    final List<BooksRecord> records = converter2.convert(ar.result());
                    System.out.println(records);
                    ctx.verify(() -> Assertions.assertEquals(records.size(), 2));
                } else {
                    ctx.failNow(ar.cause());
                }
            } finally {
                flag.flag();
            }
        });
    }

}
