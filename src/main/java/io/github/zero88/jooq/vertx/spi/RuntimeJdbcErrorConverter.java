package io.github.zero88.jooq.vertx.spi;

import org.jooq.exception.DataAccessException;

import io.github.zero88.jooq.vertx.JooqErrorConverter;
import io.vertx.ext.sql.RuntimeSQLException;

public final class RuntimeJdbcErrorConverter implements JooqErrorConverter<RuntimeSQLException> {

    @Override
    public DataAccessException apply(RuntimeSQLException e) {
        return new DataAccessException(e.getMessage(), e);
    }

    @Override
    public Class<RuntimeSQLException> throwableType() {
        return RuntimeSQLException.class;
    }

}
