package io.zero88.jooqx.spi.h2;

import org.h2.Driver;
import org.jetbrains.annotations.NotNull;

import io.zero88.jooqx.provider.DBEmbeddedProvider.DBMemoryProvider;

import lombok.NonNull;

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
    default @NonNull String driverClassName() {
        return Driver.class.getName();
    }

}
