package io.zero88.jooqx.spi;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;

import io.zero88.jooqx.JooqErrorConverter;
import io.vertx.mssqlclient.MSSQLException;

public final class MSSQLErrorConverter implements JooqErrorConverter<MSSQLException> {

    @Override
    public DataAccessException apply(MSSQLException e) {
        return new DataAccessException(e.getMessage(),
                                       new SQLException(e.getMessage(), String.valueOf(e.state()), e.number(), e));
    }

    @Override
    public Class<MSSQLException> throwableType() {
        return MSSQLException.class;
    }

}
