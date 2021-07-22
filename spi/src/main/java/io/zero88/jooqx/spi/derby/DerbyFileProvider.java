package io.zero88.jooqx.spi.derby;

import org.jetbrains.annotations.NotNull;

import io.zero88.jooqx.provider.DBEmbeddedProvider.DBFileProvider;

import lombok.NonNull;

/**
 * Provides H2 database in local file
 *
 * @see DBFileProvider
 * @since 1.1.0
 */
public interface DerbyFileProvider extends DBFileProvider {

    @Override
    default @NotNull String protocol() {
        return "jdbc:derby:";
    }

    @Override
    default @NonNull String driverClassName() {
        return "org.apache.derby.jdbc.EmbeddedDriver";
    }

}
