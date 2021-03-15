package io.zero88.jooqx.spi.mssql;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;

import io.vertx.mssqlclient.MSSQLException;
import io.zero88.jooqx.JooqErrorConverter;

public final class MSSQLErrorConverter implements JooqErrorConverter<MSSQLException> {

    @Override
    public DataAccessException transform(MSSQLException e) {
        return new DataAccessException(e.getMessage(),
                                       new SQLException(e.getMessage(), String.valueOf(e.state()), e.number(), e));
    }

    @Override
    public Class<MSSQLException> throwableType() {
        return MSSQLException.class;
    }

}
