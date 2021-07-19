package io.zero88.jooqx;

import io.zero88.jooqx.JooqErrorConverter.JDBCErrorConverter;
import io.zero88.jooqx.provider.ErrorConverterCreator;

public interface UseJdbcErrorConverter extends ErrorConverterCreator {

    @Override
    default SQLErrorConverter errorConverter() {
        return new JDBCErrorConverter();
    }

}
