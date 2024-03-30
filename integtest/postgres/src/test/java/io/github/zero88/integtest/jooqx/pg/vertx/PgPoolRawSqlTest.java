package io.github.zero88.integtest.jooqx.pg.vertx;

import org.jooq.JSONB;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PgUseVertxType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.spi.pg.PgPoolProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;

@SuppressWarnings("JUnitMalformedDeclaration")
class PgPoolRawSqlTest extends PgSQLJooqxTest<PgPool>
    implements PgSQLErrorConverterProvider, PgPoolProvider, PgUseVertxType {

    @Override
    public boolean alreadyGenerated() {
        return false;
    }

    @Test
    void test_select_to_JsonArray(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        jooqx.sqlQuery("select '[\"test\"]'::jsonb", DSLAdapter.fetchOne(DSL.field("test", JSONB.class)))
             .onSuccess(rec -> ctx.verify(() -> {
                 final Object value = rec.getValue(0);
                 Assertions.assertInstanceOf(JsonArray.class, value);
                 Assertions.assertEquals(new JsonArray().add("test"), value);
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_select_to_JsonObject(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        jooqx.sqlQuery("select '{\"test\": 123}'::jsonb", DSLAdapter.fetchOne(DSL.field("test", JSONB.class)))
             .onSuccess(rec -> ctx.verify(() -> {
                 final Object value = rec.getValue(0);
                 Assertions.assertInstanceOf(JsonObject.class, value);
                 Assertions.assertEquals(new JsonObject().put("test", 123), value);
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

}
