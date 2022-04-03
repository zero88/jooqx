package io.zero88.jooqx.spi.sqlite;

import org.jetbrains.annotations.NotNull;

import io.zero88.jooqx.provider.DBEmbeddedProvider.DBFileProvider;

/**
 * Provides SQLite database in file
 *
 * @see DBFileProvider
 * @since 1.0.0
 */
public interface SQLiteFileProvider extends DBFileProvider {

    @Override
    default @NotNull String driverClassName() {
        return "org.sqlite.JDBC";
    }

    @Override
    default @NotNull String protocol() {
        return "jdbc:sqlite:";
    }

}
