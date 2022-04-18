package io.github.zero88.integtest.jooqx.pg;

import java.util.Arrays;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jooq.SQLDialect;
import org.jooq.Schema;
import org.jooq.impl.EnumConverter;

import io.github.zero88.jooqx.JooqSQL;
import io.github.zero88.jooqx.SQLConnectionOption;
import io.github.zero88.jooqx.SQLTestHelper;
import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.datatype.JooqxConverter;
import io.github.zero88.jooqx.datatype.basic.UDTParser;
import io.github.zero88.jooqx.provider.TypeMapperRegistryProvider;
import io.github.zero88.jooqx.spi.pg.datatype.PgTypeMapperRegistry;
import io.github.zero88.sample.model.pgsql.DefaultCatalog;
import io.github.zero88.sample.model.pgsql.Public;
import io.github.zero88.sample.model.pgsql.enums.Mood;
import io.github.zero88.sample.model.pgsql.enums.Weather;
import io.github.zero88.sample.model.pgsql.udt.FullAddress;
import io.github.zero88.sample.model.pgsql.udt.records.FullAddressRecord;
import io.vertx.junit5.VertxTestContext;

public interface PostgreSQLHelper<S extends Schema> extends JooqSQL<S>, SQLTestHelper {

    default void prepareDatabase(VertxTestContext context, JooqSQL<?> jooqSql, SQLConnectionOption connOption,
        String... otherDataFiles) {
        SQLTestHelper.super.prepareDatabase(context, jooqSql, connOption,
                                            Stream.concat(Stream.of("pg_schema.sql"), Stream.of(otherDataFiles))
                                                  .toArray(String[]::new));
    }

    @Override
    default @NotNull SQLDialect dialect() {
        return SQLDialect.POSTGRES;
    }

    interface PgLegacyType extends PostgreSQLHelper<Public> {

        @Override
        default Public schema() {
            return DefaultCatalog.DEFAULT_CATALOG.PUBLIC;
        }

    }


    interface PgUseJooqType extends TypeMapperRegistryProvider, PostgreSQLHelper<Public> {

        @Override
        default DataTypeMapperRegistry typeMapperRegistry() {
            return PgTypeMapperRegistry.useUserTypeAsJooqType();
        }

        @Override
        default Public schema() {
            return DefaultCatalog.DEFAULT_CATALOG.PUBLIC;
        }

    }


    interface PgUseVertxType extends TypeMapperRegistryProvider, PostgreSQLHelper<io.github.zero88.sample.model.pgsql2.Public> {

        @Override
        default DataTypeMapperRegistry typeMapperRegistry() {
            if (alreadyGenerated()) {
                return TypeMapperRegistryProvider.super.typeMapperRegistry();
            }
            return PgTypeMapperRegistry.useUserTypeAsVertxType();
        }

        @Override
        default io.github.zero88.sample.model.pgsql2.Public schema() {
            return io.github.zero88.sample.model.pgsql2.DefaultCatalog.DEFAULT_CATALOG.PUBLIC;
        }

        boolean alreadyGenerated();

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
        public @NotNull Class<String> fromType() {
            return String.class;
        }

        @Override
        public @NotNull Class<FullAddressRecord> toType() {
            return FullAddressRecord.class;
        }

    }

}
