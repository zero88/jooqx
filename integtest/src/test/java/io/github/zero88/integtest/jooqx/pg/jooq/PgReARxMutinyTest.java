package io.github.zero88.integtest.jooqx.pg.jooq;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PostgreSQLHelper.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JooqxTestDefinition.JooqxRxHelper;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolAgroalProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.sample.data.pgsql.tables.Books;

class PgReARxMutinyTest extends PgSQLJooqxTest<JDBCPool>
    implements PgUseJooqType, JDBCPoolAgroalProvider, JDBCErrorConverterProvider, JooqxRxHelper {

    io.github.zero88.jooqx.mutiny.Jooqx jooqxMutiny;

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/book_author.sql");
        io.vertx.mutiny.jdbcclient.JDBCPool pool = io.vertx.mutiny.jdbcclient.JDBCPool.newInstance(jooqx.sqlClient());
        jooqxMutiny = io.github.zero88.jooqx.mutiny.JooqxBuilder.newInstance(io.github.zero88.jooqx.Jooqx.builder())
                                                                .setVertx(io.vertx.mutiny.core.Vertx.newInstance(vertx))
                                                                .setDSL(jooqx.dsl())
                                                                .setSqlClient(pool)
                                                                .build();
    }

    @Test
    void test_query(VertxTestContext ctx) {
        final Books table = schema().BOOKS;
        Checkpoint cp = ctx.checkpoint();
        jooqxMutiny.execute(jooqx.dsl().selectFrom(table), DSLAdapter.fetchJsonRecords(table))
                   .subscribe()
                   .with(recs -> {
                       ctx.verify(() -> Assertions.assertEquals(7, recs.size()));
                       cp.flag();
                   }, ctx::failNow);
    }

}
