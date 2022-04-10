package io.zero88.jooqx.spi.hsqldb;

import org.jetbrains.annotations.NotNull;

import io.zero88.jooqx.provider.DBEmbeddedProvider.DBMemoryProvider;

/**
 * Provides HSQLDB in memory
 *
 * @see DBMemoryProvider
 * @since 2.0.0
 */
public interface HSQLDBMemProvider extends DBMemoryProvider {

    @Override
    default @NotNull String protocol() {
        return "jdbc:hsqldb:mem:";
    }

    @Override
    default @NotNull String driverClassName() {
        return "org.hsqldb.jdbc.JDBCDriver";
    }

}
