package io.zero88.jooqx;

public interface ErrorConverterCreator {

    default SQLErrorConverter<? extends Throwable, ? extends RuntimeException> errorConverter() {
        return SQLErrorConverter.DEFAULT;
    }

}
