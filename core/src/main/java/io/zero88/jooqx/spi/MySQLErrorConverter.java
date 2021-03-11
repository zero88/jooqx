package io.zero88.jooqx.spi;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;

import io.zero88.jooqx.JooqErrorConverter;
import io.vertx.mysqlclient.MySQLException;

public final class MySQLErrorConverter implements JooqErrorConverter<MySQLException> {

    @Override
    public DataAccessException apply(MySQLException e) {
        return new DataAccessException(e.getMessage(),
                                       new SQLException(e.getMessage(), e.getSqlState(), e.getErrorCode(), e));
    }

    @Override
    public Class<MySQLException> throwableType() {
        return MySQLException.class;
    }

}
