package io.github.zero88.jooq.vertx.spi;

import java.sql.SQLException;

import org.jooq.exception.DataAccessException;

import io.github.zero88.jooq.vertx.JooqErrorConverter;

public final class JDBCErrorConverter implements JooqErrorConverter<SQLException> {

    @Override
    public DataAccessException apply(SQLException throwable) {
        return new DataAccessException(throwable.getMessage(), throwable);
    }

    @Override
    public Class<SQLException> throwableType() {
        return SQLException.class;
    }

}
