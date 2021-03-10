package io.github.zero88.jooq.vertx;

import java.sql.SQLException;
import java.util.function.Function;

import org.jooq.exception.DataAccessException;

import io.github.zero88.jooq.vertx.spi.JdbcErrorConverter;

import lombok.NonNull;

/**
 * Represents for SQL error maker that transforms SQL exception to {@link DataAccessException}
 *
 * @param <A> Type of Throwable
 * @see DataAccessException
 * @since 1.0.0
 */
public interface JooqErrorConverter<A extends Throwable> extends SqlErrorConverter<A, DataAccessException> {

    default <T extends RuntimeException> SqlErrorConverter<A, T> to(@NonNull Function<DataAccessException, T> to) {
        return new SqlErrorConverter<A, T>() {
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
            return new JdbcErrorConverter().handle(t);
        }
        return DEFAULT.handle(t);
    }

}
