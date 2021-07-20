package io.zero88.jooqx.spi.pg;

import io.zero88.jooqx.SQLErrorConverter;
import io.zero88.jooqx.provider.ErrorConverterProvider;

/**
 * PostgreSQL error converter provider
 *
 * @see PgErrorConverter
 * @since 1.1.0
 */
public interface PgSQLErrorConverterProvider extends ErrorConverterProvider {

    @Override
    default SQLErrorConverter errorConverter() {
        return new PgErrorConverter();
    }

}
