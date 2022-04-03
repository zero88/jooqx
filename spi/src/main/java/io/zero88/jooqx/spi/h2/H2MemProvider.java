package io.zero88.jooqx.spi.h2;

import org.jetbrains.annotations.NotNull;

import io.zero88.jooqx.provider.DBEmbeddedProvider.DBMemoryProvider;

/**
 * Provides H2 database in memory
 *
 * @see DBMemoryProvider
 * @since 1.1.0
 */
public interface H2MemProvider extends DBMemoryProvider {

    @Override
    default @NotNull String protocol() {
        return "jdbc:h2:mem:";
    }

    @Override
    default @NotNull String driverClassName() {
        return "org.h2.Driver";
    }

}
