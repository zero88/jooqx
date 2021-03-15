package io.zero88.jooqx;

import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

public interface TypeMapperRegistryCreator {

    default DataTypeMapperRegistry typeMapperRegistry() {
        return new DataTypeMapperRegistry();
    }

}
