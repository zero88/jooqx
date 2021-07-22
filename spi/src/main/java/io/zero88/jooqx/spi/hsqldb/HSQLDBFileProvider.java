package io.zero88.jooqx.spi.hsqldb;

import org.jetbrains.annotations.NotNull;

import io.zero88.jooqx.provider.DBEmbeddedProvider.DBFileProvider;

import lombok.NonNull;

/**
 * Provides HSQLDB in local file
 *
 * @see DBFileProvider
 * @since 1.1.0
 */
public interface HSQLDBFileProvider extends DBFileProvider {

    @Override
    default @NotNull String protocol() {
        return "jdbc:hsqldb:file:";
    }

    @Override
    default @NonNull String driverClassName() {
        return "org.hsqldb.jdbc.JDBCDriver";
    }

}
