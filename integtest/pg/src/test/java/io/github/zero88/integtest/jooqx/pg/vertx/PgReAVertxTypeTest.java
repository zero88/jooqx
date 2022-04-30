package io.github.zero88.integtest.jooqx.pg.vertx;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PostgreSQLHelper.PgUseVertxType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.spi.pg.PgPoolProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.sample.model.pgsql.tables.VertxAllDataTypes;
import io.github.zero88.sample.model.pgsql.tables.records.VertxAllDataTypesRecord;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.vertx.pgclient.data.Interval;

class PgReAVertxTypeTest extends PgSQLJooqxTest<PgPool>
    implements PgSQLErrorConverterProvider, PgPoolProvider, PgUseVertxType {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/character.sql", "pg_data/temporal.sql", "pg_data/json.sql");
    }

    @Override
    public boolean alreadyGenerated() {
        return true;
    }

    @Test
    void test_query_temporal(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxAllDataTypes table = schema().VERTX_ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(31)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final VertxAllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);
                          Assertions.assertNotNull(record.getFInterval());
                          cp.flag();
                      }));
    }

    @Test
    void test_insert_interval(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxAllDataTypes table = schema().VERTX_ALL_DATA_TYPES;
        final Interval interval = Interval.of(1, 2, 3, 4, 5, 6, 1000);
        jooqx.execute(jooqx.dsl().insertInto(table, table.ID, table.F_INTERVAL).values(10, interval).returning(),
                      DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
                final VertxAllDataTypesRecord record = assertSuccess(ctx, ar);
                System.out.println(record);
                Assertions.assertEquals(interval, record.getFInterval());
                cp.flag();
            }));
    }

    @Test
    void test_query_buffer(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxAllDataTypes table = schema().VERTX_ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(21)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final VertxAllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);
                          Assertions.assertNotNull(record.getFMiscBytea());
                          Assertions.assertEquals("HELLO", record.getFMiscBytea().toString());
                          cp.flag();
                      }));
    }

    @Test
    void test_json_object_json_array(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxAllDataTypes table = schema().VERTX_ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(51)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final VertxAllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);
                          Assertions.assertNotNull(record.getFJsonObject());
                          Assertions.assertEquals(new JsonObject(
                                                      "{\"str\":\"blah\",\"int\":1,\"float\":3.5,\"object\":{}," +
                                                      "\"array\":[]}"),
                                                  record.getFJsonObject());
                          Assertions.assertNotNull(record.getFJsonArray());
                          Assertions.assertEquals(new JsonArray("[1,true,null,9.5,\"Hi\"]"), record.getFJsonArray());
                          cp.flag();
                      }));
    }

    @Test
    void test_jsonb_object_json_array(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxAllDataTypes table = schema().VERTX_ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(61)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final VertxAllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);
                          Assertions.assertNotNull(record.getFJsonbObject());
                          Assertions.assertEquals(new JsonObject(
                                                      "{\"str\":\"blah\",\"int\":1,\"float\":3.5,\"object\":{}," +
                                                      "\"array\":[]}"),
                                                  record.getFJsonbObject());
                          Assertions.assertNotNull(record.getFJsonbArray());
                          Assertions.assertEquals(new JsonArray("[1,true,null,9.5,\"Hi\"]"), record.getFJsonbArray());
                          cp.flag();
                      }));
    }

    @Test
    void test_field_interval_but_covert_to_duration(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxAllDataTypes table = schema().VERTX_ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(31)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final VertxAllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);
                          Assertions.assertEquals(Duration.parse("PT4H33M59S"), record.getFDuration());
                          cp.flag();
                      }));
    }

}
