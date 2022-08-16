package io.github.zero88.integtest.jooqx.mysql;

import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.BlockQuery;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.spi.mysql.MySQLJooqxTest;
import io.github.zero88.jooqx.spi.mysql.MySQLPoolProvider;
import io.github.zero88.sample.model.mysql.Tables;
import io.github.zero88.sample.model.mysql.tables.Authors;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mysqlclient.MySQLPool;

class MySQLReAQueryTest extends MySQLJooqxTest<MySQLPool> implements MySQLPoolProvider, MySQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, connOpt, "mysql_data/book_author.sql");
    }

    @Test
    void test_query_authors(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final Authors table = schema().AUTHORS;
        jooqx.execute(dsl -> dsl.selectFrom(table), DSLAdapter.fetchJsonRecords(table))
             .onSuccess(recs -> ctx.verify(() -> {
                 Assertions.assertEquals(8, recs.size());
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_query_count(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        jooqx.execute(dsl -> dsl.select(DSL.count().as("count")).from(Tables.AUTHORS), DSLAdapter.fetchCount())
             .onSuccess(r -> ctx.verify(() -> {
                 System.out.println(r);
                 Assertions.assertEquals(8, r);
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_query_exist(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        jooqx.execute(dsl -> dsl.selectOne()
                                .whereExists(
                                    dsl.selectFrom(Tables.AUTHORS).where(Tables.AUTHORS.NAME.eq("Christian Wenz"))),
                      DSLAdapter.fetchExists()).onSuccess(r -> ctx.verify(() -> {
            Assertions.assertTrue(r);
            cp.flag();
        })).onFailure(ctx::failNow);
    }

    @Test
    @Disabled("Unsupported: MySQL unable execute multiple queries?")
    void test_select_block(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        jooqx.block(dsl -> BlockQuery.create()
                                     .add(dsl.select().from(schema().AUTHORS), DSLAdapter.fetchMany(schema().AUTHORS))
                                     .add(dsl.select().from(schema().BOOKS), DSLAdapter.fetchMany(schema().BOOKS)))
             .onSuccess(r -> ctx.verify(() -> {
                 flag.flag();
             }))
             .onFailure(ctx::failNow);
    }

}
