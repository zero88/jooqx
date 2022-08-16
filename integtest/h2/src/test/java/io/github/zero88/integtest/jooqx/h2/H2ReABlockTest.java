package io.github.zero88.integtest.jooqx.h2;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.BlockQuery;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JooqxTestDefinition.JooqxDBMemoryTest;
import io.github.zero88.jooqx.spi.h2.H2MemProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.github.zero88.sample.model.h2.tables.records.AuthorRecord;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class H2ReABlockTest extends JooqxDBMemoryTest<JDBCPool> implements H2MemProvider, H2SQLHelper, JDBCPoolHikariProvider {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "h2_data/book_author.sql");
    }

    @Test
    @Disabled("BUG: Only get result of first query")
    void test_select_block(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        jooqx.block(dsl -> BlockQuery.create()
                                     .add(dsl.select().from(schema().AUTHORS), DSLAdapter.fetchMany(schema().AUTHORS))
                                     .add(dsl.select().from(schema().BOOKS), DSLAdapter.fetchMany(schema().BOOKS)))
             .onSuccess(r -> ctx.verify(() -> {
                 final List<AuthorRecord> records = r.get(0);
                 Assertions.assertEquals(8, records.size());
                 System.out.println(records);
                 Assertions.assertEquals(2, r.size());
                 flag.flag();
             }))
             .onFailure(ctx::failNow);
    }

}
