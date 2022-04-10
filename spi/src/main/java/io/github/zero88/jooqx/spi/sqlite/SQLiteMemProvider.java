package io.github.zero88.jooqx.spi.sqlite;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.jooqx.provider.DBEmbeddedProvider.DBMemoryProvider;

/**
 * Provides SQLite database in memory
 *
 * @see DBMemoryProvider
 * @since 2.0.0
 */
public interface SQLiteMemProvider extends DBMemoryProvider {

    @Override
    default @NotNull String driverClassName() {
        return "org.sqlite.JDBC";
    }

    @Override
    default @NotNull String protocol() {
        return "jdbc:sqlite::memory:";
    }

}
