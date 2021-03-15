package io.zero88.jooqx.spi.db2;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;

import io.vertx.db2client.DB2Exception;
import io.zero88.jooqx.JooqErrorConverter;

public final class DB2ErrorConverter implements JooqErrorConverter<DB2Exception> {

    @Override
    public DataAccessException transform(DB2Exception e) {
        return new DataAccessException(e.getMessage(),
                                       new SQLException(e.getMessage(), e.getSqlState(), e.getErrorCode(),
                                                        e.getCause()));
    }

    @Override
    public Class<DB2Exception> throwableType() {
        return DB2Exception.class;
    }

}
