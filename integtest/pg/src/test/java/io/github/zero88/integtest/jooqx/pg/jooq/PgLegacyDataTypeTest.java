package io.github.zero88.integtest.jooqx.pg.jooq;

import org.jooq.JSONB;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.EnumMoodConverter;
import io.github.zero88.integtest.jooqx.pg.EnumWeatherConverter;
import io.github.zero88.integtest.jooqx.pg.FullAddressConverter;
import io.github.zero88.integtest.jooqx.pg.JDBCIntervalConverter;
import io.github.zero88.integtest.jooqx.pg.JDBCJsonConverter;
import io.github.zero88.integtest.jooqx.pg.JDBCJsonbConverter;
import io.github.zero88.integtest.jooqx.pg.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.datatype.UserTypeAsJooqType;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLLegacyTest;
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
import io.vertx.sqlclient.Tuple;

@SuppressWarnings("JUnitMalformedDeclaration")
class PgLegacyDataTypeTest extends PgSQLLegacyTest implements JDBCErrorConverterProvider, PgUseJooqType {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/json.sql", "pg_data/numeric.sql", "pg_data/temporal.sql",
                             "pg_data/udt.sql");
    }

    @Override
    public DataTypeMapperRegistry typeMapperRegistry() {
        return super.typeMapperRegistry()
                    .add(UserTypeAsJooqType.create(new JDBCJsonConverter()))
                    .add(UserTypeAsJooqType.create(new JDBCJsonbConverter()))
                    .addByColumn(schema().ALL_DATA_TYPES.F_INTERVAL,
                                 UserTypeAsJooqType.create(new JDBCIntervalConverter()))
                    .addByColumn(schema().ALL_DATA_TYPES.F_UDT_MOOD, UserTypeAsJooqType.create(new EnumMoodConverter()))
                    .addByColumn(schema().ALL_DATA_TYPES.F_UDT_WEATHER,
                                 UserTypeAsJooqType.create(new EnumWeatherConverter()))
                    .addByColumn(schema().ALL_DATA_TYPES.F_UDT_ADDRESS,
                                 UserTypeAsJooqType.create(new FullAddressConverter()));
    }

    @Test
    void test_simple(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        jooqx.sqlQuery("select '[\"test\"]'::jsonb", DSLAdapter.fetchOne(DSL.field("test", JSONB.class)))
             .onSuccess(rec -> ctx.verify(() -> {
                 final Object value = rec.getValue(0);
                 Assertions.assertInstanceOf(JSONB.class, value);
                 Assertions.assertEquals(JSONB.jsonb("[\"test\"]"), value);
                 cp.flag();
             }))
             .onFailure(ctx::failNow);
    }

    @Test
    void test_query_numeric(VertxTestContext ctx) {
        Checkpoint cp = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(1)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final AllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);

                          Assertions.assertNotNull(record.getFBoolean());

                          Assertions.assertNotNull(record.getFNumInt());
                          Assertions.assertNotNull(record.getFNumLong());

                          Assertions.assertNotNull(record.getFNumFloat4());
                          Assertions.assertNotNull(record.getFNumDouble());

                          Assertions.assertNotNull(record.getFNumSerial());
                          Assertions.assertNotNull(record.getFNumBigSerial());
                          Assertions.assertNotNull(record.getFNumSmallSerial());

                          Assertions.assertNotNull(record.getFNumShort());
                          Assertions.assertNotNull(record.getFNumNumeric());
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

                          JsonObject data = new JsonObject(
                              "{\"str\":\"blah\", \"int\" : 1, \"float\" : 3.5, \"object\": {}, \"array\" : []}");
                          Assertions.assertEquals(data, new JsonObject(record.getFJsonObject().data()));
                          Assertions.assertEquals(new JsonArray("[1,true,null,9.5,\"Hi\"]"),
                                                  new JsonArray(record.getFJsonArray().data()));

                          Assertions.assertEquals("4", record.getFJsonNumber().data());
                          Assertions.assertEquals("\"Hello World\"", record.getFJsonString().data());
                          Assertions.assertEquals("true", record.getFJsonBooleanTrue().data());
                          Assertions.assertEquals("false", record.getFJsonBooleanFalse().data());
                          Assertions.assertEquals(Tuple.JSON_NULL.toString(), record.getFJsonNullValue().data());
                          Assertions.assertEquals(Tuple.JSON_NULL.toString(), record.getFJsonNull().data());

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
                          System.out.println(record.getFJsonbObject());
                          Assertions.assertNotNull(record.getFJsonbArray());

                          Assertions.assertEquals("4", record.getFJsonbNumber().data());
                          Assertions.assertEquals("\"Hello World\"", record.getFJsonbString().data());
                          Assertions.assertEquals("true", record.getFJsonbBooleanTrue().data());
                          Assertions.assertEquals("false", record.getFJsonbBooleanFalse().data());
                          Assertions.assertEquals(Tuple.JSON_NULL.toString(), record.getFJsonbNullValue().data());
                          Assertions.assertEquals(Tuple.JSON_NULL.toString(), record.getFJsonbNull().data());
                          cp.flag();
                      }));
    }

    @Test
    void test_query_temporal(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(31)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final AllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);
                          Assertions.assertNotNull(record.getFDate());
                          Assertions.assertNotNull(record.getFTime());
                          Assertions.assertNotNull(record.getFTimetz());
                          Assertions.assertNotNull(record.getFTimestamptz());
                          Assertions.assertNotNull(record.getFTimestamp());
                          Assertions.assertNotNull(record.getFInterval());
                          Assertions.assertEquals(10, record.getFInterval().getYears());
                          Assertions.assertEquals(3, record.getFInterval().getMonths());
                          Assertions.assertEquals(332, record.getFInterval().getDays());
                          Assertions.assertEquals(20, record.getFInterval().getHours());
                          Assertions.assertEquals(20, record.getFInterval().getMinutes());
                          Assertions.assertEquals(20, record.getFInterval().getSeconds());
                          Assertions.assertEquals(999999, record.getFInterval().getMicro());
                          flag.flag();
                      }));
    }

    @Test
    void test_query_enum(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(43)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final AllDataTypesRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);

                          Assertions.assertEquals(Mood.ok, record.getFUdtMood());
                          Assertions.assertEquals(Weather.sunny, record.getFUdtWeather());
                          flag.flag();
                      }));
    }

    @Test
    void test_query_udt(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        final AllDataTypes table = schema().ALL_DATA_TYPES;
        jooqx.execute(dsl -> dsl.selectFrom(table).where(table.ID.eq(41)).limit(1), DSLAdapter.fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final AllDataTypesRecord record = assertSuccess(ctx, ar);
                          final FullAddressRecord address = record.getFUdtAddress();
                          Assertions.assertNotNull(address);
                          Assertions.assertEquals("US Open", address.getState());
                          Assertions.assertEquals("Any,town", address.getCity());
                          Assertions.assertEquals("", address.getStreet());
                          Assertions.assertEquals(10, address.getNoa());
                          Assertions.assertEquals(true, address.getHome());
                          flag.flag();
                      }));
    }

}
