package io.github.zero88.integtest.jooqx.pg;

import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.github.zero88.jooqx.provider.TypeMapperRegistryProvider;
import io.github.zero88.jooqx.spi.pg.datatype.PgTypeMapperRegistry;

public interface PgUseVertxType
    extends TypeMapperRegistryProvider, PostgreSQLHelper<io.github.zero88.sample.model.pgsql.Public> {

    @Override
    default DataTypeMapperRegistry typeMapperRegistry() {
        if (alreadyGenerated()) {
            return TypeMapperRegistryProvider.super.typeMapperRegistry();
        }
        return PgTypeMapperRegistry.useUserTypeAsVertxType();
    }

    @Override
    default io.github.zero88.sample.model.pgsql.Public schema() {
        return io.github.zero88.sample.model.pgsql.DefaultCatalog.DEFAULT_CATALOG.PUBLIC;
    }

    boolean alreadyGenerated();

}
