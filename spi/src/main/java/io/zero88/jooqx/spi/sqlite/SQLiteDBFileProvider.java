package io.zero88.jooqx.spi.sqlite;

import org.jetbrains.annotations.NotNull;
import org.sqlite.JDBC;

import io.zero88.jooqx.provider.DBEmbeddedProvider.DBFileProvider;

import lombok.NonNull;

/**
 * Provides SQLite database in file
 *
 * @see DBFileProvider
 * @since 1.0.0
 */
public interface SQLiteDBFileProvider extends DBFileProvider {

    @Override
    default @NonNull String driverClassName() {
        return JDBC.class.getName();
    }

    @Override
    default @NotNull String protocol() {
        return "jdbc:sqlite:";
    }

}
