package io.zero88.jooqx.spi.mysql;

import org.jetbrains.annotations.Nullable;

import io.zero88.jooqx.SQLErrorConverter;
import io.zero88.jooqx.provider.ErrorConverterProvider;

/**
 * MySQL error converter provider
 *
 * @see MySQLErrorConverter
 * @since 1.1.0
 */
public interface MySQLErrorConverterProvider extends ErrorConverterProvider {

    @Override
    default @Nullable SQLErrorConverter errorConverter() {
        return new MySQLErrorConverter();
    }

}
