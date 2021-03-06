package io.github.zero88.jooq.vertx;

import java.util.function.Function;

import org.jooq.exception.DataAccessException;

public class ErrorHandler<E extends RuntimeException> implements Function<Throwable, E> {

    @Override
    public E apply(Throwable throwable) {
        return (E) new DataAccessException("xx", throwable);
    }

}
