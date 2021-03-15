package io.zero88.jooqx.integtest.spi.pg;

import java.util.Arrays;

import org.jooq.impl.EnumConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgPool;
import io.zero88.jooqx.ReactiveDSL;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.zero88.jooqx.datatype.JooqxConverter;
import io.zero88.jooqx.datatype.UserTypeAsJooqType;
import io.zero88.jooqx.datatype.basic.UDTParser;
import io.zero88.jooqx.integtest.pgsql.enums.Mood;
import io.zero88.jooqx.integtest.pgsql.enums.Weather;
import io.zero88.jooqx.integtest.pgsql.tables.EnumDataType;
import io.zero88.jooqx.integtest.pgsql.tables.UdtDataType;
import io.zero88.jooqx.integtest.pgsql.tables.records.EnumDataTypeRecord;
import io.zero88.jooqx.integtest.pgsql.tables.records.UdtDataTypeRecord;
import io.zero88.jooqx.integtest.pgsql.udt.FullAddress;
import io.zero88.jooqx.integtest.pgsql.udt.records.FullAddressRecord;
import io.zero88.jooqx.integtest.spi.pg.PostgreSQLHelper.PgUseJooqType;
import io.zero88.jooqx.spi.pg.PgPoolProvider;
import io.zero88.jooqx.spi.pg.PgSQLReactiveTest;
import io.zero88.jooqx.spi.pg.UsePgSQLErrorConverter;

class PgReACustomTest extends PgSQLReactiveTest<PgPool>
    implements UsePgSQLErrorConverter, PgPoolProvider, PostgreSQLHelper, PgUseJooqType {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/custom.sql");
    }

    @Override
    public DataTypeMapperRegistry typeMapperRegistry() {
        return PgUseJooqType.super.typeMapperRegistry()
                                  .addByColumn(catalog().PUBLIC.ENUM_DATA_TYPE.CURRENTMOOD.getName(),
                                               UserTypeAsJooqType.create(new EnumMoodConverter()))
                                  .addByColumn(catalog().PUBLIC.ENUM_DATA_TYPE.CURRENTWEATHER.getName(),
                                               UserTypeAsJooqType.create(new EnumWeatherConverter()))
                                  .addByColumn(catalog().PUBLIC.UDT_DATA_TYPE.ADDRESS.getName(),
                                               UserTypeAsJooqType.create(new FullAddressConverter()));
    }

    @Test
    void queryEnum(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        final EnumDataType table = catalog().PUBLIC.ENUM_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), ReactiveDSL.adapter().fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final EnumDataTypeRecord record = assertSuccess(ctx, ar);
                          System.out.println(record);

                          Assertions.assertEquals(Mood.ok, record.getCurrentmood());
                          Assertions.assertEquals(Weather.sunny, record.getCurrentweather());
                          flag.flag();
                      }));
    }

    @Test
    void queryCustom(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        final UdtDataType table = catalog().PUBLIC.UDT_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), ReactiveDSL.adapter().fetchOne(table),
                      ar -> ctx.verify(() -> {
                          final UdtDataTypeRecord record = assertSuccess(ctx, ar);
                          Assertions.assertNotNull(record.getAddress());
                          Assertions.assertEquals("US Open", record.getAddress().getState());
                          Assertions.assertEquals("Any,town", record.getAddress().getCity());
                          Assertions.assertEquals("", record.getAddress().getStreet());
                          Assertions.assertEquals(10, record.getAddress().getNoa());
                          Assertions.assertEquals(true, record.getAddress().getHome());
                          flag.flag();
                      }));
    }

    public static class EnumMoodConverter extends EnumConverter<String, Mood> implements JooqxConverter<String, Mood> {

        public EnumMoodConverter() {
            super(String.class, Mood.class);
        }

    }


    public static class EnumWeatherConverter extends EnumConverter<String, Weather>
        implements JooqxConverter<String, Weather> {

        public EnumWeatherConverter() {
            super(String.class, Weather.class);
        }

    }


    public static class FullAddressConverter implements JooqxConverter<String, FullAddressRecord> {

        @Override
        public FullAddressRecord from(String vertxObject) {
            String[] udt = UDTParser.parse(vertxObject);
            if (udt == null) {
                return null;
            }
            System.out.println(vertxObject + "::" + Arrays.toString(udt));
            return new FullAddressRecord().with(FullAddress.STATE, udt[0])
                                          .with(FullAddress.CITY, udt[1])
                                          .with(FullAddress.STREET, udt[2])
                                          .with(FullAddress.NOA, Integer.valueOf(udt[3]))
                                          .with(FullAddress.HOME, "t".equals(udt[4]));
        }

        @Override
        public String to(FullAddressRecord jooqObject) {
            return jooqObject.toString();
        }

        @Override
        public Class<String> fromType() {
            return String.class;
        }

        @Override
        public Class<FullAddressRecord> toType() {
            return FullAddressRecord.class;
        }

    }

}
