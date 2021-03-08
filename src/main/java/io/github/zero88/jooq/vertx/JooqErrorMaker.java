package io.github.zero88.jooq.vertx;

import java.util.function.Function;

import org.jooq.exception.DataAccessException;

import lombok.NonNull;

/**
 * Represents for SQL error maker that transforms SQL exception to {@link DataAccessException}
 *
 * @param <A> Type of Throwable
 * @see DataAccessException
 * @since 1.0.0
 */
public interface JooqErrorMaker<A extends Throwable> extends SqlErrorMaker<A, DataAccessException> {

    default <T extends RuntimeException> SqlErrorMaker<A, T> to(@NonNull Function<DataAccessException, T> to) {
        return new SqlErrorMaker<A, T>() {
            @Override
            public Class<A> throwableType() { return JooqErrorMaker.this.throwableType(); }

            @Override
            public T apply(A throwable) { return JooqErrorMaker.this.andThen(to).apply(throwable); }
        };
    }

}
