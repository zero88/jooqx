package io.zero88.jooqx.integtest.spi.pg;

import java.util.stream.Stream;

import org.jooq.SQLDialect;

import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.JooqSQL;
import io.zero88.jooqx.SQLConnectionOption;
import io.zero88.jooqx.SQLTestHelper;
import io.zero88.jooqx.TypeMapperRegistryCreator;
import io.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.zero88.jooqx.integtest.pgsql.DefaultCatalog;
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

}
