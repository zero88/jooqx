package io.github.zero88.jooqx.spi.db2;

import java.sql.SQLException;

import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;

import io.github.zero88.jooqx.JooqErrorConverter;
import io.vertx.db2client.DB2Exception;

/**
 * DB2 error converter
 *
 * @see DB2Exception
 * @since 1.0.0
 */
public final class DB2ErrorConverter implements JooqErrorConverter<DB2Exception> {

    @Override
    public @NotNull DataAccessException transform(DB2Exception e) {
        return new DataAccessException(e.getMessage(),
                                       new SQLException(e.getMessage(), e.getSqlState(), e.getErrorCode(),
                                                        e.getCause()));
    }

    @Override
    public @NotNull Class<DB2Exception> throwableType() {
        return DB2Exception.class;
    }

}
