package io.github.zero88.jooqx.spi.pg;

import io.github.zero88.jooqx.SQLErrorConverter;
import io.github.zero88.jooqx.provider.ErrorConverterProvider;

/**
 * PostgreSQL error converter provider
 *
 * @see PgErrorConverter
 * @since 2.0.0
 */
public interface PgSQLErrorConverterProvider extends ErrorConverterProvider {

    @Override
    default SQLErrorConverter errorConverter() {
        return new PgErrorConverter();
    }

}
