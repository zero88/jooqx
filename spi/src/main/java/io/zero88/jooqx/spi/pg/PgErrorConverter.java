package io.zero88.jooqx.spi.pg;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;

import io.vertx.pgclient.PgException;
import io.zero88.jooqx.JooqErrorConverter;

import lombok.NonNull;

/**
 * PostgreSQL error converter
 *
 * @see PgException
 * @since 1.0.0
 */
public final class PgErrorConverter implements JooqErrorConverter<PgException> {

    @Override
    public @NonNull DataAccessException transform(PgException e) {
        return new DataAccessException(e.getErrorMessage(), new SQLException(e.getErrorMessage(), e.getCode(), e));
    }

    @Override
    public @NonNull Class<PgException> throwableType() {
        return PgException.class;
    }

}
