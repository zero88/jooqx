package io.github.zero88.jooqx.spi.mysql;

import org.jetbrains.annotations.Nullable;

import io.github.zero88.jooqx.SQLErrorConverter;
import io.github.zero88.jooqx.provider.ErrorConverterProvider;

/**
 * MySQL error converter provider
 *
 * @see MySQLErrorConverter
 * @since 2.0.0
 */
public interface MySQLErrorConverterProvider extends ErrorConverterProvider {

    @Override
    default @Nullable SQLErrorConverter errorConverter() {
        return new MySQLErrorConverter();
    }

}
