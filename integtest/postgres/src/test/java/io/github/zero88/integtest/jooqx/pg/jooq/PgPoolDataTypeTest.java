package io.github.zero88.integtest.jooqx.pg.jooq;

import java.nio.charset.StandardCharsets;
import java.time.Period;

import org.jooq.types.YearToSecond;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.EnumMoodConverter;
import io.github.zero88.integtest.jooqx.pg.EnumWeatherConverter;
import io.github.zero88.integtest.jooqx.pg.FullAddressConverter;
import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.datatype.UserTypeAsJooqType;
import io.github.zero88.jooqx.datatype.basic.JsonArrayJSONBConverter;
import io.github.zero88.jooqx.datatype.basic.JsonArrayJSONConverter;
import io.github.zero88.jooqx.spi.pg.PgPoolProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLJooqxTest;
import io.github.zero88.jooqx.spi.pg.datatype.IntervalConverter;
import io.github.zero88.sample.model.pgsql.enums.Mood;
import io.github.zero88.sample.model.pgsql.enums.Weather;
import io.github.zero88.sample.model.pgsql.tables.AllDataTypes;
import io.github.zero88.sample.model.pgsql.tables.records.AllDataTypesRecord;
import io.github.zero88.sample.model.pgsql.udt.records.FullAddressRecord;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Tuple;

class PgPoolDataTypeTest extends PgSQLJooqxTest<PgPool>
    implements PgSQLErrorConverterProvider, PgPoolProvider, PgUseJooqType {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/character.sql", "pg_data/geometric.sql", "pg_data/json.sql",
                             "pg_data/numeric.sql", "pg_data/temporal.sql", "pg_data/udt.sql", "pg_data/array.sql");
    }

    @Override
    public DataTypeMapperRegistry typeMapperRegistry() {
        return PgUseJooqType.super.typeMapperRegistry()
                                  .addByColumn(schema().ALL_DATA_TYPES.F_JSON_ARRAY,
                                               UserTypeAsJooqType.create(new JsonArrayJSONConverter()))
                                  .addByColumn(schema().ALL_DATA_TYPES.F_JSONB_ARRAY,
                                               UserTypeAsJooqType.create(new JsonArrayJSONBConverter()))
                                  .addByColumn(schema().ALL_DATA_TYPES.F_INTERVAL,
                                               UserTypeAsJooqType.create(new IntervalConverter()))
                                  .addByColumn(schema().ALL_DATA_TYPES.F_UDT_MOOD,
                                               UserTypeAsJooqType.create(new EnumMoodConverter()))
                                  .addByColumn(schema().ALL_DATA_TYPES.F_UDT_WEATHER,
                                               UserTypeAsJooqType.create(new EnumWeatherConverter()))
                                  .addByColumn(schema().ALL_DATA_TYPES.F_UDT_ADDRESS,
                                               UserTypeAsJooqType.create(new FullAddressConverter()));
    }

    @Test
    void test_query_character(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.fetchOne(dsl -> dsl.selectFrom(table).where(table.ID.eq(21)).limit(1), ar -> ctx.verify(() -> {
            final AllDataTypesRecord record = assertSuccess(ctx, ar);
            System.out.println(record);

            Assertions.assertNotNull(record.getFStrFixedChar());
            Assertions.assertNotNull(record.getFStrChar());

            Assertions.assertNotNull(record.getFStrText());
            Assertions.assertNotNull(record.getFStrVarchar());

            Assertions.assertNotNull(record.getFMiscName());
            Assertions.assertNotNull(record.getFMiscUuid());
            Assertions.assertNotNull(record.getFMiscBytea());
            Assertions.assertEquals("HELLO", new String(record.getFMiscBytea(), StandardCharsets.UTF_8));
            flag.flag();
        }));
    }

    @Test
    void test_query_geometric(VertxTestContext ctx) {
        final Checkpoint cp = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.fetchOne(dsl -> dsl.selectFrom(table).where(table.ID.eq(71)).limit(1), ar -> ctx.verify(() -> {
            final AllDataTypesRecord record = assertSuccess(ctx, ar);
            System.out.println(record);

            Assertions.assertNotNull(record.getFPoint());

            Assertions.assertNotNull(record.getFPolygon());
            Assertions.assertNotNull(record.getFBox());

            Assertions.assertNotNull(record.getFCircle());
            Assertions.assertNotNull(record.getFLine());
            Assertions.assertNotNull(record.getFLseg());
            Assertions.assertNotNull(record.getFClosedPath());
            Assertions.assertNotNull(record.getFOpenedPath());
            cp.flag();
        }));
    }

    @Test
    void test_query_json(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(51)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final AllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);
                          JsonObject data = new JsonObject("{\"str\":\"blah\", \"int\" : 1, \"float\" : 3.5, " +
                                                           "\"object\": {}, \"array\" : []   }");
                          Assertions.assertEquals(data.encode(), record.getFJsonObject().data());
                          Assertions.assertEquals(new JsonArray("[1,true,null,9.5,\"Hi\"]").encode(),
                                                  record.getFJsonArray().data());

                          Assertions.assertEquals(4, table.F_JSON_NUMBER.coerce(Integer.class).get(record));
                          Assertions.assertEquals("Hello World", table.F_JSON_STRING.coerce(String.class).get(record));
                          Assertions.assertEquals(true, table.F_JSON_BOOLEAN_TRUE.coerce(Boolean.class).get(record));
                          Assertions.assertEquals(false, table.F_JSON_BOOLEAN_FALSE.coerce(Boolean.class).get(record));
                          Assertions.assertEquals(Tuple.JSON_NULL,
                                                  table.F_JSON_NULL_VALUE.coerce(Object.class).get(record));
                          Assertions.assertNull(record.getFJsonNull());

                          cp.flag();
                      }));
    }

    @Test
    void test_query_jsonb(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(61)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final AllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);

                          Assertions.assertNotNull(record.getFJsonbObject());
                          Assertions.assertNotNull(record.getFJsonbArray());
                          Assertions.assertNull(record.getFJsonbNull());

                          Assertions.assertEquals(4, table.F_JSONB_NUMBER.coerce(Integer.class).get(record));
                          Assertions.assertEquals("Hello World", table.F_JSONB_STRING.coerce(String.class).get(record));
                          Assertions.assertEquals(true, table.F_JSONB_BOOLEAN_TRUE.coerce(Boolean.class).get(record));
                          Assertions.assertEquals(false, table.F_JSONB_BOOLEAN_FALSE.coerce(Boolean.class).get(record));
                          Assertions.assertEquals(Tuple.JSON_NULL,
                                                  table.F_JSONB_NULL_VALUE.coerce(Object.class).get(record));
                          cp.flag();
                      }));
    }

    @Test
    void test_query_numeric(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.fetchOne(jooqx.dsl().selectFrom(table).where(table.ID.eq(1)).limit(1), ar -> ctx.verify(() -> {
            final AllDataTypesRecord record = assertSuccess(ctx, ar);
            System.out.println(record);

            Assertions.assertNotNull(record.getFBoolean());

            Assertions.assertNotNull(record.getFNumInt());
            Assertions.assertNotNull(record.getFNumLong());

            Assertions.assertNotNull(record.getFNumShort());
            Assertions.assertNotNull(record.getFNumSerial());
            Assertions.assertNotNull(record.getFNumSmallSerial());
            Assertions.assertNotNull(record.getFNumBigSerial());

            Assertions.assertNotNull(record.getFNumFloat4());
            Assertions.assertNotNull(record.getFNumDouble());
            Assertions.assertNotNull(record.getFNumNumeric());
            cp.flag();
        }));
    }

    @Test
    void test_query_temporal(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.fetchOne(dsl -> dsl.selectFrom(table).where(table.ID.eq(31)).limit(1), ar -> ctx.verify(() -> {
            final AllDataTypesRecord record = assertSuccess(ctx, ar);
            System.out.println(record);
            Assertions.assertNotNull(record.getFDate());
            Assertions.assertNotNull(record.getFTime());
            Assertions.assertNotNull(record.getFTimetz());
            Assertions.assertNotNull(record.getFTimestamp());
            Assertions.assertNotNull(record.getFTimestamptz());
            Assertions.assertNotNull(record.getFInterval());
            Assertions.assertEquals(10, record.getFInterval().getYears());
            Assertions.assertEquals(3, record.getFInterval().getMonths());
            Assertions.assertEquals(332, record.getFInterval().getDays());
            cp.flag();
        }));
    }

    @Test
    void test_insert_interval(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        final YearToSecond value1 = YearToSecond.valueOf(Period.of(1, 2, 3));
        jooqx.execute(dsl -> dsl.insertInto(table, table.ID, table.F_INTERVAL).values(10, value1).returning(),
                      DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
                final AllDataTypesRecord record = assertSuccess(ctx, ar);
                System.out.println(record);
                Assertions.assertEquals(value1, record.getFInterval());
                cp.flag();
            }));
    }

    @Test
    void test_query_enum(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(43)).limit(1), DSLAdapter.fetchOne(table))
             .onSuccess(record -> ctx.verify(() -> {
                 System.out.println(record);
                 Assertions.assertEquals(Mood.ok, record.getFUdtMood());
                 Assertions.assertEquals(Weather.sunny, record.getFUdtWeather());
                 flag.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_query_udt(VertxTestContext ctx) {
        final Checkpoint flag = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(41)), DSLAdapter.fetchOne(table))
             .onSuccess(record -> ctx.verify(() -> {
                 System.out.println(record);
                 final FullAddressRecord address = record.getFUdtAddress();
                 Assertions.assertNotNull(address);
                 Assertions.assertEquals("US Open", address.getState());
                 Assertions.assertEquals("Any,town", address.getCity());
                 Assertions.assertEquals("", address.getStreet());
                 Assertions.assertEquals(10, address.getNoa());
                 Assertions.assertEquals(true, address.getHome());
                 flag.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_query_array(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        final AllDataTypes tbl = schema().ALL_DATA_TYPES;
        jooqx.fetchOne(dsl -> dsl.select(tbl.F_ONE_DIMENSION, tbl.F_TWO_DIMENSION).from(tbl).where(tbl.ID.eq(81)))
             .onSuccess(record -> ctx.verify(() -> {
                 Assertions.assertArrayEquals(new Integer[] { 20000, 25000, 25000, 25000 }, record.value1());
                 flag.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_insert_array(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        final AllDataTypes t = schema().ALL_DATA_TYPES;
        jooqx.insert(dsl -> dsl.insertInto(t, t.ID, t.F_ONE_DIMENSION).values(85, new Integer[] { 1, 2, 3 }))
             .flatMap(r -> jooqx.fetchOne(dsl -> dsl.select(t.ID, t.F_ONE_DIMENSION).from(t).where(t.ID.eq(85))))
             .onSuccess(record -> ctx.verify(() -> {
                 final Object[] oneDimension = (Object[]) record.get(1);
                 Assertions.assertArrayEquals(new Integer[] { 1, 2, 3 }, oneDimension);
                 flag.flag();
             }))
             .onFailure(ctx::failNow);
    }

}
