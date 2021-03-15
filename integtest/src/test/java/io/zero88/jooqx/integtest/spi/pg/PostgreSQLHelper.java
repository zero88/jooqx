package io.zero88.jooqx.integtest.spi.pg;

import java.util.Arrays;
import java.util.stream.Stream;

import org.jooq.SQLDialect;
import org.jooq.impl.EnumConverter;

import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.JooqSQL;
import io.zero88.jooqx.SQLConnectionOption;
import io.zero88.jooqx.SQLTestHelper;
import io.zero88.jooqx.TypeMapperRegistryCreator;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.zero88.jooqx.datatype.JooqxConverter;
import io.zero88.jooqx.datatype.basic.UDTParser;
import io.zero88.jooqx.integtest.pgsql.DefaultCatalog;
import io.zero88.jooqx.integtest.pgsql.enums.Mood;
import io.zero88.jooqx.integtest.pgsql.enums.Weather;
import io.zero88.jooqx.integtest.pgsql.udt.FullAddress;
import io.zero88.jooqx.integtest.pgsql.udt.records.FullAddressRecord;
import io.zero88.jooqx.spi.pg.datatype.PgTypeMapperRegistry;

import lombok.NonNull;

public interface PostgreSQLHelper extends JooqSQL<DefaultCatalog>, SQLTestHelper {

    default void prepareDatabase(VertxTestContext context, JooqSQL<?> jooqSql, SQLConnectionOption connOption,
                                 String... otherDataFiles) {
        SQLTestHelper.super.prepareDatabase(context, jooqSql, connOption,
                                            Stream.concat(Stream.of("pg_schema.sql"), Stream.of(otherDataFiles))
                                                  .toArray(String[]::new));
    }

    @Override
    default DefaultCatalog catalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    default @NonNull SQLDialect dialect() {
        return SQLDialect.POSTGRES;
    }

    interface PgUseJooqType extends TypeMapperRegistryCreator {

        @Override
        default DataTypeMapperRegistry typeMapperRegistry() {
            return PgTypeMapperRegistry.useUserTypeAsJooqType();
        }

    }


    interface PgUseVertxType extends TypeMapperRegistryCreator {

        @Override
        default DataTypeMapperRegistry typeMapperRegistry() {
            return PgTypeMapperRegistry.useUserTypeAsVertxType();
        }

    }


    class EnumMoodConverter extends EnumConverter<String, Mood> implements JooqxConverter<String, Mood> {

        public EnumMoodConverter() {
            super(String.class, Mood.class);
        }

    }


    class EnumWeatherConverter extends EnumConverter<String, Weather> implements JooqxConverter<String, Weather> {

        public EnumWeatherConverter() {
            super(String.class, Weather.class);
        }

    }


    class FullAddressConverter implements JooqxConverter<String, FullAddressRecord> {

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
