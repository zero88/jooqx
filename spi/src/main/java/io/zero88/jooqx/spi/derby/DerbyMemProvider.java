package io.zero88.jooqx.spi.derby;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.apache.derby.shared.common.error.DerbySQLIntegrityConstraintViolationException;
import org.h2.Driver;
import org.jetbrains.annotations.NotNull;

import io.zero88.jooqx.provider.DBEmbeddedProvider.DBMemoryProvider;

import lombok.NonNull;

/**
 * Provides Derby database in memory
 *
 * @see DBMemoryProvider
 * @since 1.1.0
 */
public interface DerbyMemProvider extends DBMemoryProvider {

    @Override
    default @NotNull String protocol() {
        return "jdbc:derby:memory:";
    }

    @Override
    default @NonNull String driverClassName() {
        return EmbeddedDriver.class.getName();
    }

}