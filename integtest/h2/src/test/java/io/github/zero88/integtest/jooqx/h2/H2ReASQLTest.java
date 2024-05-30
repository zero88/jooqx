package io.github.zero88.integtest.jooqx.h2;

import org.jooq.meta.h2.information_schema.Tables;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JooqxTestDefinition.JooqxDBMemoryTest;
import io.github.zero88.jooqx.spi.h2.H2MemProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.github.zero88.sample.model.h2.tables.Author;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

class H2ReASQLTest extends JooqxDBMemoryTest<JDBCPool> implements H2MemProvider, H2SQLHelper, JDBCPoolHikariProvider {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        prepareDatabase(ctx, this, connOpt);
    }

    @Test
    void test_create_table_plain_sql(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();

        jooqx.sql("CREATE TABLE HELLO_JOOQX (id INT)")
             .onSuccess(r -> Assertions.assertEquals(0, r, "Create table is failed, return err " + r))
             .flatMap(i -> jooqx.fetchExists(dsl -> dsl.select(Tables.TABLES.TABLE_NAME)
                                                       .from(Tables.TABLES)
                                                       .where(Tables.TABLES.TABLE_NAME.eq("HELLO_JOOQX"))))
             .onSuccess(c -> ctx.verify(() -> {
                 Assertions.assertTrue(c, "Table 'HELLO_JOOQX' is not yet exists'");
                 flag.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_query_table_by_plain_sql(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        jooqx.sqlQuery("SELECT * FROM " + schema().AUTHOR, DSLAdapter.fetchMany(Author.AUTHOR))
             .onSuccess(c -> ctx.verify(() -> {
                 Assertions.assertEquals(0, c.size());
                 flag.flag();
             }))
             .onFailure(ctx::failNow);
    }

}
