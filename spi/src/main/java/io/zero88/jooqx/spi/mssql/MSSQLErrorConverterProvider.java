package io.zero88.jooqx.spi.mssql;

import org.jetbrains.annotations.Nullable;

import io.zero88.jooqx.SQLErrorConverter;
import io.zero88.jooqx.provider.ErrorConverterProvider;

/**
 * MSSQL error converter provider
 *
 * @see MSSQLErrorConverter
 * @since 1.1.0
 */
public interface MSSQLErrorConverterProvider extends ErrorConverterProvider {

    @Override
    default @Nullable SQLErrorConverter errorConverter() {
        return new MSSQLErrorConverter();
    }

}
