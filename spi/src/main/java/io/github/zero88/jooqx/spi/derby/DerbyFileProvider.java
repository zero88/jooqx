package io.github.zero88.jooqx.spi.derby;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.jooqx.provider.DBEmbeddedProvider.DBFileProvider;

/**
 * Provides H2 database in local file
 *
 * @see DBFileProvider
 * @since 2.0.0
 */
public interface DerbyFileProvider extends DBFileProvider {

    @Override
    default @NotNull String protocol() {
        return "jdbc:derby:";
    }

    @Override
    default @NotNull String driverClassName() {
        return "org.apache.derby.jdbc.EmbeddedDriver";
    }

}
