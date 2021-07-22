package io.zero88.jooqx.spi.h2;

import org.h2.Driver;
import org.jetbrains.annotations.NotNull;

import io.zero88.jooqx.provider.DBEmbeddedProvider.DBFileProvider;

import lombok.NonNull;

/**
 * Provides H2 database in local file
 *
 * @see DBFileProvider
 * @since 1.1.0
 */
public interface H2FileProvider extends DBFileProvider {

    @Override
    default @NotNull String protocol() {
        return "jdbc:h2:file:";
    }

    @Override
    default @NonNull String driverClassName() {
        return Driver.class.getName();
    }

}
