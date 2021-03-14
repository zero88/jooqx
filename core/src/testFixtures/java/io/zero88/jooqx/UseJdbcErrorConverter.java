package io.zero88.jooqx;

import io.zero88.jooqx.JooqErrorConverter.JDBCErrorConverter;

public interface UseJdbcErrorConverter extends ErrorConverterCreator {

    @Override
    default SQLErrorConverter errorConverter() {
        return new JDBCErrorConverter();
    }

}
