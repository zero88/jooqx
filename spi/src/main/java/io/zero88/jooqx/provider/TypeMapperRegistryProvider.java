package io.zero88.jooqx.provider;

import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

/**
 * Data Type mapper registry provider
 *
 * @see DataTypeMapperRegistry
 * @since 1.1.0
 */
public interface TypeMapperRegistryProvider {

    default DataTypeMapperRegistry typeMapperRegistry() {
        return new DataTypeMapperRegistry();
    }

}
