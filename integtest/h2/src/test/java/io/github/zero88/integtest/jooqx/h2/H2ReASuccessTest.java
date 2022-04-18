package io.github.zero88.integtest.jooqx.h2;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JooqxTestDefinition.JooqxDBMemoryTest;
import io.github.zero88.jooqx.spi.h2.H2MemProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.github.zero88.sample.model.h2.tables.Alldatatypes;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class H2ReASuccessTest extends JooqxDBMemoryTest<JDBCPool>
    implements H2MemProvider, H2SQLHelper, JDBCPoolHikariProvider {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        //        jooqx.execute(dsl->prepareDatabase(ctx, dsl, "h2_schema.sql"));
        prepareDatabase(ctx, this, connOpt);
    }

    @Test
    void test_insert_query_timestamp(VertxTestContext testContext) {
        Checkpoint flag = testContext.checkpoint();
        final Alldatatypes table = schema().ALLDATATYPES;
        final LocalDateTime ldt = LocalDateTime.of(2021, 7, 23, 19, 0);
        jooqx.execute(dsl -> dsl.insertInto(table, table.F_TIMESTAMP).values(ldt).returning(),
                      DSLAdapter.fetchOne(table))
             .onFailure(testContext::failNow)
             .flatMap(r -> jooqx.execute(dsl -> dsl.selectFrom(table), DSLAdapter.fetchMany(table)))
             .onComplete(testContext.succeeding(rows -> {
                 Assertions.assertTrue(rows.size() > 0);
                 Assertions.assertEquals(ldt, rows.get(0).getFTimestamp());
                 flag.flag();
             }));
    }

}
