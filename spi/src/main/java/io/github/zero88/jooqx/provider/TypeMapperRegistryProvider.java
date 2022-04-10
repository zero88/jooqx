package io.github.zero88.jooqx.provider;

import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;

/**
 * Data Type mapper registry provider
 *
 * @see DataTypeMapperRegistry
 * @since 2.0.0
 */
public interface TypeMapperRegistryProvider {

    default DataTypeMapperRegistry typeMapperRegistry() {
        return new DataTypeMapperRegistry();
    }

}
