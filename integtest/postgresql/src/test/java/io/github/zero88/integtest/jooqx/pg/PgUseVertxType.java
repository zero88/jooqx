package io.github.zero88.integtest.jooqx.pg;

import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.provider.TypeMapperRegistryProvider;
import io.github.zero88.jooqx.spi.pg.datatype.PgTypeMapperRegistry;
import io.github.zero88.sample.model.pgsql.DefaultCatalog;
import io.github.zero88.sample.model.pgsql.Public;

public interface PgUseVertxType extends TypeMapperRegistryProvider, PostgreSQLHelper<Public> {

    @Override
    default DataTypeMapperRegistry typeMapperRegistry() {
        if (alreadyGenerated()) {
            return TypeMapperRegistryProvider.super.typeMapperRegistry();
        }
        return PgTypeMapperRegistry.useUserTypeAsVertxType();
    }

    @Override
    default Public schema() {
        return DefaultCatalog.DEFAULT_CATALOG.PUBLIC;
    }

    boolean alreadyGenerated();

}
