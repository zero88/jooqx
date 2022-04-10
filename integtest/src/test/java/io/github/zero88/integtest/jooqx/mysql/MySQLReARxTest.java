package io.github.zero88.integtest.jooqx.mysql;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JooqxTestDefinition.JooqxRxHelper;
import io.github.zero88.jooqx.spi.mysql.MySQLJooqxTest;
import io.github.zero88.jooqx.spi.mysql.MySQLPoolProvider;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.mysqlclient.MySQLPool;
import io.zero88.sample.data.mysql.tables.Authors;

@Disabled
//FIXME: Don't understand why it doesnt connect
public class MySQLReARxTest extends MySQLJooqxTest<MySQLPool> implements MySQLPoolProvider, MySQLHelper, JooqxRxHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "mysql_schema.sql", "mysql_data/book_author.sql");
    }

    @Test
    void test_query_authors(VertxTestContext ctx) {
        final Authors table = schema().AUTHORS;
        Checkpoint cp = ctx.checkpoint();
        rxPool(jooqx).rxExecute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchJsonRecords(table)).subscribe(recs -> {
            ctx.verify(() -> Assertions.assertEquals(7, recs.size()));
            cp.flag();
        }, ctx::failNow);
    }

}
