package io.zero88.jooqx.spi.sqlite;

import org.jetbrains.annotations.NotNull;
import org.sqlite.JDBC;

import io.zero88.jooqx.provider.DBEmbeddedProvider.DBMemoryProvider;

import lombok.NonNull;

/**
 * Provides SQLite database in memory
 *
 * @see DBMemoryProvider
 * @since 1.1.0
 */
public interface SQLiteDBMemProvider extends DBMemoryProvider {

    @Override
    default @NonNull String driverClassName() {
        return JDBC.class.getName();
    }

    @Override
    default @NotNull String protocol() {
        return "jdbc:sqlite::memory:";
    }

}
