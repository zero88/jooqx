package io.github.zero88.integtest.jooqx.pg.jooq;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.zero88.integtest.jooqx.pg.PostgreSQLHelper.PgUseJooqType;
import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.datatype.UserTypeAsJooqType;
import io.github.zero88.jooqx.spi.jdbc.JDBCErrorConverterProvider;
import io.github.zero88.jooqx.spi.pg.PgSQLLegacyTest;
import io.vertx.core.Vertx;
import io.vertx.junit5.Checkpoint;
import io.vertx.junit5.VertxTestContext;
import io.zero88.sample.data.pgsql.enums.Mood;
import io.zero88.sample.data.pgsql.enums.Weather;
import io.zero88.sample.data.pgsql.tables.EnumDataType;
import io.zero88.sample.data.pgsql.tables.UdtDataType;
import io.zero88.sample.data.pgsql.tables.records.EnumDataTypeRecord;
import io.zero88.sample.data.pgsql.tables.records.UdtDataTypeRecord;

class PgLeGCustomTest extends PgSQLLegacyTest implements JDBCErrorConverterProvider, PgUseJooqType {

    @Override
    @BeforeEach
    public void tearUp(Vertx vertx, VertxTestContext ctx) {
        super.tearUp(vertx, ctx);
        this.prepareDatabase(ctx, this, connOpt, "pg_data/custom.sql");
    }

    @Override
    public DataTypeMapperRegistry typeMapperRegistry() {
        return PgUseJooqType.super.typeMapperRegistry()
                                  .addByColumn(schema().ENUM_DATA_TYPE.CURRENTMOOD,
                                               UserTypeAsJooqType.create(new EnumMoodConverter()))
                                  .addByColumn(schema().ENUM_DATA_TYPE.CURRENTWEATHER,
                                               UserTypeAsJooqType.create(new EnumWeatherConverter()))
                                  .addByColumn(schema().UDT_DATA_TYPE.ADDRESS,
                                               UserTypeAsJooqType.create(new FullAddressConverter()));
    }

    @Test
    void queryEnum(VertxTestContext ctx) {
        Checkpoint flag = ctx.checkpoint();
        final EnumDataType table = schema().ENUM_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
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
        final UdtDataType table = schema().UDT_DATA_TYPE;
        jooqx.execute(jooqx.dsl().selectFrom(table).limit(1), DSLAdapter.fetchOne(table), ar -> ctx.verify(() -> {
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

}