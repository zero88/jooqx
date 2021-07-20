package io.zero88.jooqx;

import java.sql.SQLException;

import org.jetbrains.annotations.NotNull;
import org.jooq.exception.DataAccessException;

import lombok.NonNull;

/**
 * Represents for SQL error maker that transforms SQL exception to {@code jOOQ} {@link DataAccessException}
 *
 * @param <T> Type of Throwable
 * @see DataAccessException
 * @since 1.0.0
 */
public interface JooqErrorConverter<T extends Throwable> extends SQLErrorConverter {

    @NonNull DataAccessException transform(T t);

    @NonNull Class<T> throwableType();

    @Override
    default RuntimeException handle(Throwable throwable) {
        if (throwableType().isInstance(throwable)) {
            return transform(throwableType().cast(throwable));
        }
        if (throwable instanceof SQLException) {
            return new JDBCErrorConverter().handle(throwable);
        }
        return SQLErrorConverter.super.handle(throwable);
    }

    /**
     * JDBC error converter
     *
     * @see SQLException
     * @since 1.0.0
     */
    class JDBCErrorConverter implements JooqErrorConverter<SQLException> {

        @Override
        public @NotNull DataAccessException transform(SQLException t) {
            return new DataAccessException(t.getMessage(), t);
        }

        @Override
        public @NonNull Class<SQLException> throwableType() {
            return SQLException.class;
        }

    }

}
