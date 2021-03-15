package io.zero88.jooqx.spi.mysql;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;

import io.vertx.mysqlclient.MySQLException;
import io.zero88.jooqx.JooqErrorConverter;

public final class MySQLErrorConverter implements JooqErrorConverter<MySQLException> {

    @Override
    public DataAccessException transform(MySQLException e) {
        return new DataAccessException(e.getMessage(),
                                       new SQLException(e.getMessage(), e.getSqlState(), e.getErrorCode(), e));
    }

    @Override
    public Class<MySQLException> throwableType() {
        return MySQLException.class;
    }

}
