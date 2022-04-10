package io.github.zero88.jooqx.spi.mysql;

import java.sql.SQLException;

import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;

import io.github.zero88.jooqx.JooqErrorConverter;
import io.vertx.mysqlclient.MySQLException;

/**
 * MySQL error converter
 *
 * @see MySQLException
 * @since 1.0.0
 */
public final class MySQLErrorConverter implements JooqErrorConverter<MySQLException> {

    @Override
    public @NotNull DataAccessException transform(MySQLException e) {
        return new DataAccessException(e.getMessage(),
                                       new SQLException(e.getMessage(), e.getSqlState(), e.getErrorCode(), e));
    }

    @Override
    public @NotNull Class<MySQLException> throwableType() {
        return MySQLException.class;
    }

}
