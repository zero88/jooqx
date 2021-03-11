package io.zero88.jooqx;

import java.sql.SQLException;
import java.util.function.Function;

import org.jooq.exception.DataAccessException;

import lombok.NonNull;

/**
 * Represents for SQL error maker that transforms SQL exception to {@code jOOQ} {@link DataAccessException}
 *
 * @param <A> Type of Throwable
 * @see DataAccessException
 * @since 1.0.0
 */
public interface JooqErrorConverter<A extends Throwable> extends SQLErrorConverter<A, DataAccessException> {

    default <T extends RuntimeException> SQLErrorConverter<A, T> to(@NonNull Function<DataAccessException, T> to) {
        return new SQLErrorConverter<A, T>() {
            @Override
            public Class<A> throwableType() { return JooqErrorConverter.this.throwableType(); }

            @Override
            public T apply(A throwable) { return JooqErrorConverter.this.andThen(to).apply(throwable); }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    default RuntimeException handle(@NonNull Throwable t) {
        if (throwableType().isInstance(t)) {
            return apply((A) t);
        }
        if (t instanceof SQLException) {
            return new JDBCErrorConverter().handle(t);
        }
        return DEFAULT.handle(t);
    }

    final class JDBCErrorConverter implements JooqErrorConverter<SQLException> {

        @Override
        public DataAccessException apply(SQLException throwable) {
            return new DataAccessException(throwable.getMessage(), throwable);
        }

        @Override
        public Class<SQLException> throwableType() {
            return SQLException.class;
        }

    }

}
