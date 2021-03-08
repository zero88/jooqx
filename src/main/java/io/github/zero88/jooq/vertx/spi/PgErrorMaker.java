package io.github.zero88.jooq.vertx.spi;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;

import io.github.zero88.jooq.vertx.JooqErrorMaker;
import io.vertx.pgclient.PgException;

public final class PgErrorMaker implements JooqErrorMaker<PgException> {

    @Override
    public DataAccessException apply(PgException e) {
        return new DataAccessException(e.getErrorMessage(), new SQLException(e.getErrorMessage(), e.getCode(), e));
    }

    @Override
    public Class<PgException> throwableType() {
        return PgException.class;
    }

}
