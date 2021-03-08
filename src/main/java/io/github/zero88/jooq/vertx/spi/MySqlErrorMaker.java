package io.github.zero88.jooq.vertx.spi;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;

import io.github.zero88.jooq.vertx.JooqErrorMaker;
import io.vertx.mysqlclient.MySQLException;

public final class MySqlErrorMaker implements JooqErrorMaker<MySQLException> {

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
