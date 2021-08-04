package io.zero88.jooqx.integtest.spi.pg.jooq;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.jooqx.JooqxTestDefinition.JooqxRxHelper;
import io.zero88.jooqx.integtest.spi.pg.PostgreSQLHelper.PgUseJooqType;
import io.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.zero88.jooqx.spi.jdbc.JDBCPoolAgroalProvider;
import io.zero88.jooqx.spi.pg.PgSQLJooqxTest;

class PgReARxTest extends PgSQLJooqxTest<JDBCPool>
    implements PgUseJooqType, JDBCPoolAgroalProvider, JDBCErrorConverterProvider, JooqxRxHelper {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql");
    }

    @Test
    void test_simple_rx(VertxTestContext ctx) {
        final io.zero88.jooqx.integtest.pgsql.tables.Books table = schema().BOOKS;
        Checkpoint cp = ctx.checkpoint();
        rxPool(jooqx).rxExecute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchJsonRecords(table)).subscribe(recs -> {
            ctx.verify(() -> Assertions.assertEquals(7, recs.size()));
            cp.flag();
        }, ctx::failNow);
    }

}
