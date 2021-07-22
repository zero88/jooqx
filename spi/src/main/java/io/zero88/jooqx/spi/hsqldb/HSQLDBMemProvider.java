package io.zero88.jooqx.spi.hsqldb;

import org.jetbrains.annotations.NotNull;

import io.zero88.jooqx.provider.DBEmbeddedProvider.DBMemoryProvider;

import lombok.NonNull;

/**
 * Provides HSQLDB in memory
 *
 * @see DBMemoryProvider
 * @since 1.1.0
 */
public interface HSQLDBMemProvider extends DBMemoryProvider {

    @Override
    default @NotNull String protocol() {
        return "jdbc:hsqldb:mem:";
    }

    @Override
    default @NonNull String driverClassName() {
        return "org.hsqldb.jdbc.JDBCDriver";
    }

}
