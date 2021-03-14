package io.zero88.jooqx.spi.pg;

import io.zero88.jooqx.ErrorConverterCreator;
import io.zero88.jooqx.SQLErrorConverter;

public interface UsePgSQLErrorConverter extends ErrorConverterCreator {

    @Override
    default SQLErrorConverter errorConverter() {
        return new PgErrorConverter();
    }

}
