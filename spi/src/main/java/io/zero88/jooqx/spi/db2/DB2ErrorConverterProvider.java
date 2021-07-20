package io.zero88.jooqx.spi.db2;

import io.zero88.jooqx.SQLErrorConverter;
import io.zero88.jooqx.provider.ErrorConverterProvider;

/**
 * DB2 error converter provider
 *
 * @see DB2ErrorConverter
 * @since 1.1.0
 */
public interface DB2ErrorConverterProvider extends ErrorConverterProvider {

    @Override
    default SQLErrorConverter errorConverter() {
        return new DB2ErrorConverter();
    }

}
