package io.github.zero88.integtest.jooqx.pg.jooq;

import java.util.Arrays;
import java.util.List;

import org.jooq.InsertResultStep;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.BindBatchValues;
import io.github.zero88.jooqx.BlockQuery;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JsonRecord;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLLegacyTest;
import io.github.zero88.sample.model.pgsql.routines.Add;
import io.github.zero88.sample.model.pgsql.tables.Books;
import io.github.zero88.sample.model.pgsql.tables.records.AuthorsRecord;
import io.github.zero88.sample.model.pgsql.tables.records.BooksRecord;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class PgLegacyRelationTest extends PgSQLLegacyTest implements PgUseJooqType, JDBCErrorConverterProvider {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql");
    }

    @Test
    void should_unsupported_routine() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> jooqx.routine(new Add()));
    }

    @Test
    void test_query(VertxTestContext ctx) {
        jooqx.fetchMany(dsl -> dsl.selectFrom(schema().BOOKS), ar -> assertResultSize(ctx, ar, 7));
    }

    @Test
    void test_insert(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        final Books table = schema().BOOKS;
        final InsertResultStep<BooksRecord> insert = jooqx.dsl()
                                                          .insertInto(table, table.ID, table.TITLE)
                                                          .values(Arrays.asList(DSL.defaultValue(table.ID), "abc"))
                                                          .returning(table.ID);
        jooqx.execute(insert, DSLAdapter.fetchJsonRecord(table), ar -> ctx.verify(() -> {
            final JsonRecord<?> jsonRecord = assertSuccess(ctx, ar);
            Assertions.assertEquals(new JsonObject().put("id", 8).put("title", null), jsonRecord.toJson());
            flag.flag();
        }));
    }

    @Test
    void test_batch_insert(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final Books table = schema().BOOKS;
        BooksRecord rec1 = new BooksRecord().setTitle("abc");
        BooksRecord rec2 = new BooksRecord().setTitle("xyz");
        BooksRecord rec3 = new BooksRecord().setTitle("qwe");

        final BindBatchValues bindValues = new BindBatchValues().register(table.TITLE).add(rec1, rec2, rec3);
        final InsertResultStep<BooksRecord> insert = jooqx.dsl()
                                                          .insertInto(table)
                                                          .set(bindValues.getDummyValues())
                                                          .returning();
        jooqx.batch(insert, bindValues, ar -> {
            ctx.verify(() -> Assertions.assertEquals(3, ar.result().getSuccesses()));
            jooqx.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchJsonRecords(table), ar2 -> ctx.verify(() -> {
                final List<JsonRecord<BooksRecord>> records = assertResultSize(ctx, ar2, 10);
                Assertions.assertEquals(new JsonObject().put("id", 8).put("title", "abc"), records.get(7).toJson());
                Assertions.assertEquals(new JsonObject().put("id", 9).put("title", "xyz"), records.get(8).toJson());
                Assertions.assertEquals(new JsonObject().put("id", 10).put("title", "qwe"), records.get(9).toJson());
                flag.flag();
            }));
        });
    }

    @Test
    void test_block_select(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        jooqx.block(dsl -> BlockQuery.create()
                                     .add(dsl.selectFrom(schema().AUTHORS).limit(3), DSLAdapter.fetchMany(schema().AUTHORS))
                                     .add(dsl.selectFrom(schema().BOOKS), DSLAdapter.fetchMany(schema().BOOKS)))
             .onSuccess(blockResult -> ctx.verify(() -> {
                 Assertions.assertEquals(2, blockResult.size());

                 final List<AuthorsRecord> records = blockResult.get(0);
                 Assertions.assertEquals(3, records.size());
                 System.out.println(records);

                 final List<BooksRecord> records2 = blockResult.get(1);
                 Assertions.assertEquals(7, records2.size());
                 System.out.println(records2);
                 flag.flag();
             }))
             .onFailure(ctx::failNow);
    }

}
