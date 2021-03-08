package io.github.zero88.jooq.vertx;

import java.util.function.Function;

import lombok.NonNull;

/**
 * Represents for a maker that unifies SQL error to a general SQL error when executing SQL command on across among
 * Database SPI
 *
 * @param <A> Type of Throwable
 * @param <E> Type of runtime exception
 * @since 1.0.0
 */
public interface SqlErrorMaker<A extends Throwable, E extends RuntimeException> extends Function<A, E> {

    SqlErrorMaker<Throwable, RuntimeException> DEFAULT = new SqlErrorMaker<Throwable, RuntimeException>() {
        @Override
        public Class<Throwable> throwableType() { return Throwable.class; }

        @Override
        public RuntimeException apply(Throwable t) {
            return t instanceof RuntimeException ? (RuntimeException) t : new RuntimeException(t);
        }
    };

    Class<A> throwableType();

    default RuntimeException handle(@NonNull Throwable t) {
        if (throwableType().isInstance(t)) {
            return apply((A) t);
        }
        return DEFAULT.handle(t);
    }

    default <X> X reThrowError(@NonNull Throwable throwable) {
        throw this.handle(throwable);
    }

}
