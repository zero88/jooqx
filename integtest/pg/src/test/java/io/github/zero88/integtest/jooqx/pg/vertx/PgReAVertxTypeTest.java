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
import io.github.zero88.sample.model.pgsql.tables.VertxCharacterDataType;
import io.github.zero88.sample.model.pgsql.tables.VertxJsonDataType;
import io.github.zero88.sample.model.pgsql.tables.VertxJsonbDataType;
import io.github.zero88.sample.model.pgsql.tables.VertxTemporalDataType;
import io.github.zero88.sample.model.pgsql.tables.records.VertxAllDataTypesRecord;
import io.github.zero88.sample.model.pgsql.tables.records.VertxCharacterDataTypeRecord;
import io.github.zero88.sample.model.pgsql.tables.records.VertxJsonDataTypeRecord;
import io.github.zero88.sample.model.pgsql.tables.records.VertxJsonbDataTypeRecord;
import io.github.zero88.sample.model.pgsql.tables.records.VertxTemporalDataTypeRecord;
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
        this.prepareDatabase(ctx, this, connOpt, "pg_data/character.sql", "pg_data/temporal.sql", "pg_data/json.sql",
                             "pg_data/all.sql");
    }

    @Override
    public boolean alreadyGenerated() {
        return true;
    }

    @Test
    void test_query_temporal(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxTemporalDataType table = schema().VERTX_TEMPORAL_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final VertxTemporalDataTypeRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertNotNull(record.getInterval());
            cp.flag();
        }));
    }

    @Test
    void test_insert_interval(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxTemporalDataType table = schema().VERTX_TEMPORAL_DATA_TYPE;
        final Interval interval = Interval.of(1, 2, 3, 4, 5, 6, 1000);
        jooqx.execute(jooqx.dsl().insertInto(table, table.ID, table.INTERVAL).values(10, interval).returning(),
                      DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
                final VertxTemporalDataTypeRecord record = assertSuccess(ctx, ar);
                System.out.println(record);
                Assertions.assertEquals(interval, record.getInterval());
                cp.flag();
            }));
    }

    @Test
    void test_query_buffer(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxCharacterDataType table = schema().VERTX_CHARACTER_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final VertxCharacterDataTypeRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertNotNull(record.getBytea());
            Assertions.assertEquals("HELLO", record.getBytea().toString());
            cp.flag();
        }));
    }

    @Test
    void test_json_object_json_array(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxJsonDataType table = schema().VERTX_JSON_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final VertxJsonDataTypeRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertNotNull(record.getJsonobject());
            Assertions.assertEquals(
                new JsonObject("{\"str\":\"blah\",\"int\":1,\"float\":3.5,\"object\":{}," + "\"array\":[]}"),
                record.getJsonobject());
            Assertions.assertNotNull(record.getJsonarray());
            Assertions.assertEquals(new JsonArray("[1,true,null,9.5,\"Hi\"]"), record.getJsonarray());
            cp.flag();
        }));
    }

    @Test
    void test_jsonb_object_json_array(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxJsonbDataType table = schema().VERTX_JSONB_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final VertxJsonbDataTypeRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertNotNull(record.getJsonobject());
            Assertions.assertEquals(
                new JsonObject("{\"str\":\"blah\",\"int\":1,\"float\":3.5,\"object\":{}," + "\"array\":[]}"),
                record.getJsonobject());
            Assertions.assertNotNull(record.getJsonarray());
            Assertions.assertEquals(new JsonArray("[1,true,null,9.5,\"Hi\"]"), record.getJsonarray());
            cp.flag();
        }));
    }

    @Test
    void test_f_interval_as_duration(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxAllDataTypes table = schema().VERTX_ALL_DATA_TYPES;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final VertxAllDataTypesRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertEquals(Duration.parse("PT4H33M59S"), record.getFInterval());
            cp.flag();
        }));
    }

}
