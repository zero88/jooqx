package io.zero88.jooqx.spi.pg;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;

import io.vertx.pgclient.PgException;
import io.zero88.jooqx.JooqErrorConverter;

public final class PgErrorConverter implements JooqErrorConverter<PgException> {

    @Override
    public DataAccessException apply(PgException e) {
        return new DataAccessException(e.getErrorMessage(), new SQLException(e.getErrorMessage(), e.getCode(), e));
    }

    @Override
    public Class<PgException> throwableType() {
        return PgException.class;
    }

}
