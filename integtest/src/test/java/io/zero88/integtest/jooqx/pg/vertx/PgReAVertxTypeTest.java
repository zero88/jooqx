package io.zero88.integtest.jooqx.pg.vertx;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.vertx.pgclient.data.Interval;
import io.zero88.integtest.jooqx.pg.PostgreSQLHelper.PgUseVertxType;
import io.zero88.jooqx.DSLAdapter;
import io.zero88.sample.data.pgsql2.tables.AllDataTypes;
import io.zero88.sample.data.pgsql2.tables.CharacterDataType;
import io.zero88.sample.data.pgsql2.tables.JsonDataType;
import io.zero88.sample.data.pgsql2.tables.JsonbDataType;
import io.zero88.sample.data.pgsql2.tables.TemporalDataType;
import io.zero88.sample.data.pgsql2.tables.records.AllDataTypesRecord;
import io.zero88.sample.data.pgsql2.tables.records.CharacterDataTypeRecord;
import io.zero88.sample.data.pgsql2.tables.records.JsonDataTypeRecord;
import io.zero88.sample.data.pgsql2.tables.records.JsonbDataTypeRecord;
import io.zero88.sample.data.pgsql2.tables.records.TemporalDataTypeRecord;
import io.zero88.jooqx.spi.pg.PgPoolProvider;
import io.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.zero88.jooqx.spi.pg.PgSQLJooqxTest;

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
        final TemporalDataType table = schema().TEMPORAL_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final TemporalDataTypeRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertNotNull(record.getInterval());
            cp.flag();
        }));
    }

    @Test
    void test_insert_interval(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final TemporalDataType table = schema().TEMPORAL_DATA_TYPE;
        final Interval interval = Interval.of(1, 2, 3, 4, 5, 6, 1000);
        jooqx.execute(jooqx.dsl().insertInto(table, table.ID, table.INTERVAL).values(10, interval).returning(),
                      DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
                final TemporalDataTypeRecord record = assertSuccess(ctx, ar);
                System.out.println(record);
                Assertions.assertEquals(interval, record.getInterval());
                cp.flag();
            }));
    }

    @Test
    void test_query_buffer(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final CharacterDataType table = schema().CHARACTER_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final CharacterDataTypeRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertNotNull(record.getBytea());
            Assertions.assertEquals("HELLO", record.getBytea().toString());
            cp.flag();
        }));
    }

    @Test
    void test_json_object_json_array(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final JsonDataType table = schema().JSON_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final JsonDataTypeRecord record = assertSuccess(ctx, ar);
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
        final JsonbDataType table = schema().JSONB_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final JsonbDataTypeRecord record = assertSuccess(ctx, ar);
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
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
            final AllDataTypesRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertEquals(Duration.parse("PT4H33M59S"), record.getFInterval());
            cp.flag();
        }));
    }

}
