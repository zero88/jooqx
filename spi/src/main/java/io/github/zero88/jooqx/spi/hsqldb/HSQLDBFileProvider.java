package io.github.zero88.jooqx.spi.hsqldb;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.jooqx.provider.DBEmbeddedProvider.DBFileProvider;

/**
 * Provides HSQLDB in local file
 *
 * @see DBFileProvider
 * @since 2.0.0
 */
public interface HSQLDBFileProvider extends DBFileProvider {

    @Override
    default @NotNull String protocol() {
        return "jdbc:hsqldb:file:";
    }

    @Override
    default @NotNull String driverClassName() {
        return "org.hsqldb.jdbc.JDBCDriver";
    }

}
