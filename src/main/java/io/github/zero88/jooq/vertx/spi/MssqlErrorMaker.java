package io.github.zero88.jooq.vertx.spi;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;

import io.github.zero88.jooq.vertx.JooqErrorMaker;
import io.vertx.mssqlclient.MSSQLException;

public final class MssqlErrorMaker implements JooqErrorMaker<MSSQLException> {

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
