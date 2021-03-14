package io.zero88.jooqx;

public interface ErrorConverterCreator {

    default SQLErrorConverter errorConverter() {
        return SQLErrorConverter.DEFAULT;
    }

}
