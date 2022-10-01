package io.github.zero88.integtest.jooqx.pg.vertx;

import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PgUseVertxType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.jdbc.JDBCPoolHikariProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;

@SuppressWarnings("JUnitMalformedDeclaration")
class PgJDBCRawSqlTest extends PgSQLJooqxTest<JDBCPool>
    implements JDBCPoolHikariProvider, JDBCErrorConverterProvider, PgUseVertxType {

    @Override
    public boolean alreadyGenerated() {
        return false;
    }

    @Test
    void test_select_to_JsonArray(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        jooqx.sqlQuery("select '[\"test\"]'::jsonb", DSLAdapter.fetchOne(DSL.field("test", String.class)))
             .onSuccess(rec -> ctx.verify(() -> {
                 final Object value = rec.getValue(0);
                 Assertions.assertInstanceOf(String.class, value);
                 Assertions.assertEquals(new JsonArray().add("test"), new JsonArray((String) value));
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_select_to_JsonObject(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        jooqx.sqlQuery("select '{\"test\": 123}'::jsonb", DSLAdapter.fetchOne(DSL.field("test", String.class)))
             .onSuccess(rec -> ctx.verify(() -> {
                 final Object value = rec.getValue(0);
                 Assertions.assertInstanceOf(String.class, value);
                 Assertions.assertEquals(new JsonObject().put("test", 123), new JsonObject((String) value));
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

}
