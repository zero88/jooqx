package io.github.zero88.integtest.jooqx.pg.vertx;

import java.time.Duration;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PgUseVertxType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.spi.pg.PgPoolProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.sample.model.pgsql.tables.VertxAllDataTypes;
import io.github.zero88.sample.model.pgsql.tables.records.VertxAllDataTypesRecord;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.jackson.DatabindCodec;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.vertx.pgclient.data.Interval;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

class PgPoolVertxTypeTest extends PgSQLJooqxTest<PgPool>
    implements PgSQLErrorConverterProvider, PgPoolProvider, PgUseVertxType {

    @BeforeAll
    static void beforeAll() {
        DatabindCodec.mapper()
                     .registerModule(new JavaTimeModule())
                     .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                              SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }

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
    void test_query_interval(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxAllDataTypes table = schema().VERTX_ALL_DATA_TYPES;
        jooqx.fetchOne(dsl -> dsl.selectFrom(table).where(table.ID.eq(31)).limit(1), ar -> ctx.verify(() -> {
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
        jooqx.execute(dsl -> dsl.insertInto(table, table.ID, table.F_INTERVAL).values(10, interval).returning(),
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
        jooqx.fetchOne(dsl -> dsl.selectFrom(table).where(table.ID.eq(21)).limit(1), ar -> ctx.verify(() -> {
            final VertxAllDataTypesRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertNotNull(record.getFMiscBytea());
            Assertions.assertEquals("HELLO", record.getFMiscBytea().toString());
            cp.flag();
        }));
    }

    @Test
    void test_query_json(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxAllDataTypes table = schema().VERTX_ALL_DATA_TYPES;
        jooqx.fetchOne(dsl -> dsl.selectFrom(table).where(table.ID.eq(51)).limit(1))
             .onSuccess(record -> ctx.verify(() -> {
                 System.out.println(record);
                 Assertions.assertNotNull(record.getFJsonObject());
                 Assertions.assertEquals(
                     new JsonObject("{\"str\":\"blah\",\"int\":1,\"float\":3.5,\"object\":{},\"array\":[]}"),
                     record.getFJsonObject());
                 Assertions.assertNotNull(record.getFJsonArray());
                 Assertions.assertEquals(new JsonArray("[1,true,null,9.5,\"Hi\"]"), record.getFJsonArray());
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_query_jsonb(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxAllDataTypes table = schema().VERTX_ALL_DATA_TYPES;
        jooqx.fetchOne(dsl -> dsl.selectFrom(table).where(table.ID.eq(61)).limit(1), ar -> ctx.verify(() -> {
            final VertxAllDataTypesRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertNotNull(record.getFJsonbObject());
            Assertions.assertEquals(
                new JsonObject("{\"str\":\"blah\",\"int\":1,\"float\":3.5,\"object\":{},\"array\":[]}"),
                record.getFJsonbObject());
            Assertions.assertNotNull(record.getFJsonbArray());
            Assertions.assertEquals(new JsonArray("[1,true,null,9.5,\"Hi\"]"), record.getFJsonbArray());
            cp.flag();
        }));
    }

    @Test
    void test_query_field_interval_but_covert_to_duration(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxAllDataTypes table = schema().VERTX_ALL_DATA_TYPES;
        jooqx.fetchOne(dsl -> dsl.selectFrom(table).where(table.ID.eq(31)).limit(1), ar -> ctx.verify(() -> {
            final VertxAllDataTypesRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertEquals(Duration.parse("PT4H33M59S"), record.getFDuration());
            cp.flag();
        }));
    }

    @Test
    void test_query_JSON_To_JsonObject(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final VertxAllDataTypes table = schema().VERTX_ALL_DATA_TYPES;
        jooqx.fetchJsonObject(dsl -> dsl.selectFrom(table).where(table.ID.eq(31)).limit(1), new DatabindCodec())
             .onSuccess(r -> ctx.verify(() -> {
                 System.out.println(r);
                 final JsonObject expected = new JsonObject(
                     "{\"id\":31,\"f_boolean\":null,\"f_num_short\":null,\"f_num_int\":null,\"f_num_long\":null," +
                     "\"f_num_float4\":null,\"f_num_double\":null,\"f_num_decimal\":null,\"f_num_numeric\":null," +
                     "\"f_num_small_serial\":3,\"f_num_serial\":3,\"f_num_big_serial\":3,\"f_str_char\":null," +
                     "\"f_str_fixed_char\":null,\"f_str_varchar\":null,\"f_str_text\":null,\"f_misc_name\":null," +
                     "\"f_misc_uuid\":null,\"f_misc_bytea\":null,\"f_date\":\"2022-05-30\",\"f_time\":\"18:00:00\"," +
                     "\"f_timetz\":\"06:00+02:00\",\"f_timestamp\":\"2022-05-14T07:00:00\"," +
                     "\"f_timestamptz\":\"2022-05-14T09:00:00Z\",\"f_interval\":{\"years\":10,\"months\":3," +
                     "\"days\":332,\"hours\":20,\"minutes\":20,\"seconds\":20,\"microseconds\":999999}," +
                     "\"f_duration\":\"PT4H33M59S\",\"f_udt_mood\":null,\"f_udt_weather\":null," +
                     "\"f_udt_address\":null,\"f_json_object\":null,\"f_json_array\":null,\"f_json_number\":null," +
                     "\"f_json_string\":null,\"f_json_boolean_true\":null,\"f_json_boolean_false\":null," +
                     "\"f_json_null_value\":null,\"f_json_null\":null,\"f_jsonb_object\":null,\"f_jsonb_array\":null," +
                     "\"f_jsonb_number\":null,\"f_jsonb_string\":null,\"f_jsonb_boolean_true\":null," +
                     "\"f_jsonb_boolean_false\":null,\"f_jsonb_null_value\":null,\"f_jsonb_null\":null," +
                     "\"f_point\":null,\"f_line\":null,\"f_lseg\":null,\"f_box\":null,\"f_closed_path\":null," +
                     "\"f_opened_path\":null,\"f_polygon\":null,\"f_circle\":null,\"f_one_dimension\":null," +
                     "\"f_two_dimension\":null}");
                 System.out.println(expected.hashCode());
                 System.out.println(r.hashCode());
                 Assertions.assertEquals(expected.encode(), r.encode());
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

}
