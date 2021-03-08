package io.github.zero88.jooq.vertx.spi;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;

import io.github.zero88.jooq.vertx.JooqErrorMaker;
import io.vertx.db2client.DB2Exception;

public final class DB2ErrorMaker implements JooqErrorMaker<DB2Exception> {

    @Override
    public DataAccessException apply(DB2Exception e) {
        return new DataAccessException(e.getMessage(),
                                       new SQLException(e.getMessage(), e.getSqlState(), e.getErrorCode(),
                                                        e.getCause()));
    }

    @Override
    public Class<DB2Exception> throwableType() {
        return DB2Exception.class;
    }

}
