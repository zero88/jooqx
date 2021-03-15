package io.zero88.jooqx.spi;

import org.jooq.exception.DataAccessException;

import io.vertx.ext.sql.RuntimeSQLException;
import io.zero88.jooqx.JooqErrorConverter;

import lombok.NonNull;

public final class RuntimeJDBCErrorConverter implements JooqErrorConverter<RuntimeSQLException> {

    @Override
    public @NonNull DataAccessException transform(RuntimeSQLException e) {
        return new DataAccessException(e.getMessage(), e);
    }

    @Override
    public @NonNull Class<RuntimeSQLException> throwableType() {
        return RuntimeSQLException.class;
    }

}
