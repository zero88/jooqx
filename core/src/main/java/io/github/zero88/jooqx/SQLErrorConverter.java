package io.github.zero88.jooqx;

import java.util.function.Function;

import org.jetbrains.annotations.NotNull;

/**
 * Represents for a maker that unifies SQL error to a general SQL error when executing SQL command on across among
 * Database SPI
 *
 * @since 1.0.0
 */
public interface SQLErrorConverter {

    SQLErrorConverter DEFAULT = new SQLErrorConverter() {};

    /**
     * Handle throwable to runtime exception
     *
     * @param throwable throwable
     * @return runtime exception that wraps given exception if the given is not runtime exception
     */
    default RuntimeException handle(Throwable throwable) {
        return throwable instanceof RuntimeException ? (RuntimeException) throwable : new RuntimeException(throwable);
    }

    /**
     * Handle and throw error
     *
     * @param throwable throwable
     * @param <X>       any type
     * @return nothing because it will throw error
     * @see #handle(Throwable)
     */
    default <X> X reThrowError(@NotNull Throwable throwable) {
        throw this.handle(throwable);
    }

    /**
     * Create new SQL error converter to easily integrate with current application exception
     *
     * @param andThen function that transform output of current {@link #handle(Throwable)} to an expectation
     * @param <R>     Type of runtime exception
     * @return new SQL error converter
     */
    default <R extends RuntimeException> SQLErrorConverter andThen(@NotNull Function<RuntimeException, R> andThen) {
        return new SQLErrorConverter() {
            @Override
            public RuntimeException handle(Throwable throwable) {
                return andThen.apply(SQLErrorConverter.this.handle(throwable));
            }
        };
    }

}
