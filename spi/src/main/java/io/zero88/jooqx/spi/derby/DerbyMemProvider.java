package io.zero88.jooqx.spi.derby;

import org.jetbrains.annotations.NotNull;

import io.zero88.jooqx.provider.DBEmbeddedProvider.DBMemoryProvider;

/**
 * Provides Derby database in memory
 *
 * @see DBMemoryProvider
 * @since 2.0.0
 */
public interface DerbyMemProvider extends DBMemoryProvider {

    @Override
    default @NotNull String protocol() {
        return "jdbc:derby:memory:";
    }

    @Override
    default @NotNull String driverClassName() {
        return "org.apache.derby.jdbc.EmbeddedDriver";
    }

}
