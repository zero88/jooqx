package io.zero88.jooqx.provider;

import org.jetbrains.annotations.Nullable;

import io.zero88.jooqx.SQLErrorConverter;

public interface ErrorConverterCreator {

    @Nullable
    default SQLErrorConverter errorConverter() {
        return SQLErrorConverter.DEFAULT;
    }

}
