package io.zero88.jooqx.spi.jdbc;

import io.zero88.jooqx.JooqErrorConverter.JDBCErrorConverter;
import io.zero88.jooqx.SQLErrorConverter;
import io.zero88.jooqx.provider.ErrorConverterProvider;

/**
 * JDBC error converter provider
 *
 * @see JDBCErrorConverter
 * @since 1.1.0
 */
public interface JDBCErrorConverterProvider extends ErrorConverterProvider {

    @Override
    default SQLErrorConverter errorConverter() {
        return new JDBCErrorConverter();
    }

}
