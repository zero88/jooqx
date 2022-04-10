package io.github.zero88.jooqx.spi.h2;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.jooqx.provider.DBEmbeddedProvider.DBFileProvider;

/**
 * Provides H2 database in local file
 *
 * @see DBFileProvider
 * @since 2.0.0
 */
public interface H2FileProvider extends DBFileProvider {

    @Override
    default @NotNull String protocol() {
        return "jdbc:h2:file:";
    }

    @Override
    default @NotNull String driverClassName() {
        return "org.h2.Driver";
    }

}
