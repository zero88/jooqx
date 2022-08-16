package io.github.zero88.integtest.jooqx.mysql;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.BlockQuery;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.github.zero88.jooqx.spi.mysql.MySQLJooqxTest;
import io.github.zero88.sample.model.mysql.Tables;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class MySQLJDBCQueryTest extends MySQLJooqxTest<JDBCPool>
    implements JDBCPoolHikariProvider, JDBCErrorConverterProvider, MySQLHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, connOpt, "mysql_data/book_author.sql");
    }

    @Override
    protected JsonObject initConnOptions() {
        final JsonObject connOpts = super.initConnOptions();
        return connOpts.put("jdbcUrl", connOpts.getValue("jdbcUrl") + "?allowMultiQueries=true");
    }

    @Test
    void test_query_count(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        jooqx.execute(dsl -> dsl.selectCount().from(Tables.BOOKS), DSLAdapter.fetchCount())
             .onSuccess(r -> ctx.verify(() -> {
                 Assertions.assertEquals(7, r);
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    @Disabled("https://github.com/vert-x3/vertx-jdbc-client/issues/276")
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
