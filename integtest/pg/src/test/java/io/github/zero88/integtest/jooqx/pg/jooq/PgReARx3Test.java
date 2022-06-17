package io.github.zero88.integtest.jooqx.pg.jooq;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.rxjava3.Jooqx;
import io.github.zero88.jooqx.rxjava3.JooqxBuilder;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolAgroalProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.sample.model.pgsql.tables.Books;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class PgReARx3Test extends PgSQLJooqxTest<JDBCPool>
    implements PgUseJooqType, JDBCPoolAgroalProvider, JDBCErrorConverterProvider {

    Jooqx jooqxRx3;

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql");
        jooqxRx3 = JooqxBuilder.newInstance(io.github.zero88.jooqx.Jooqx.builder())
                               .setVertx(io.vertx.rxjava3.core.Vertx.newInstance(vertx))
                               .setDSL(jooqx.dsl())
                               .setSqlClient(io.vertx.rxjava3.jdbcclient.JDBCPool.newInstance(jooqx.sqlClient()))
                               .build();
    }

    @Test
    void test_query(VertxTestContext ctx) {
        final Books table = schema().BOOKS;
        Checkpoint cp = ctx.checkpoint();
        jooqxRx3.rxExecute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchJsonRecords(table)).subscribe(recs -> {
            ctx.verify(() -> Assertions.assertEquals(7, recs.size()));
            cp.flag();
        }, ctx::failNow);
    }

}
