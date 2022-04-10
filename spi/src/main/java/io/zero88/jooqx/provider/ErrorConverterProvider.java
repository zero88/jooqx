package io.zero88.jooqx.provider;

import org.jetbrains.annotations.Nullable;

import io.zero88.jooqx.SQLErrorConverter;

/**
 * Provides SQL error converter
 *
 * @see SQLErrorConverter
 * @since 2.0.0
 */
public interface ErrorConverterProvider {

    @Nullable
    default SQLErrorConverter errorConverter() {
        return SQLErrorConverter.DEFAULT;
    }

}
