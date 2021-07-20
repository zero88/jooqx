package io.zero88.jooqx.spi.mssql;

import java.sql.SQLException;

import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;

import io.vertx.mssqlclient.MSSQLException;
import io.zero88.jooqx.JooqErrorConverter;

/**
 * MSSQL error converter
 *
 * @see MSSQLException
 * @since 1.0.0
 */
public final class MSSQLErrorConverter implements JooqErrorConverter<MSSQLException> {

    @Override
    public @NotNull DataAccessException transform(MSSQLException e) {
        return new DataAccessException(e.getMessage(),
                                       new SQLException(e.getMessage(), String.valueOf(e.state()), e.number(), e));
    }

    @Override
    public @NotNull Class<MSSQLException> throwableType() {
        return MSSQLException.class;
    }

}
