package io.zero88.jooqx.spi;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;

import io.zero88.jooqx.JooqErrorConverter;
import io.vertx.pgclient.PgException;

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
