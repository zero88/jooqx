package io.github.zero88.jooqx.spi;

import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;

import io.github.zero88.jooqx.JooqErrorConverter;
import io.vertx.ext.sql.RuntimeSQLException;

public final class RuntimeJDBCErrorConverter implements JooqErrorConverter<RuntimeSQLException> {

    @Override
    public @NotNull DataAccessException transform(RuntimeSQLException e) {
        return new DataAccessException(e.getMessage(), e);
    }

    @Override
    public @NotNull Class<RuntimeSQLException> throwableType() {
        return RuntimeSQLException.class;
    }

}
