package io.zero88.jooqx.spi.h2;

import java.util.UUID;

import org.h2.Driver;

import io.zero88.jooqx.DBProvider.DBMemoryProvider;
import io.zero88.jooqx.HasDBProvider;

import lombok.NonNull;

public interface H2DBProvider extends DBMemoryProvider, HasDBProvider<String, DBMemoryProvider> {

    @Override
    default @NonNull String driverClassName() {
        return Driver.class.getName();
    }

    @Override
    default String get() {
        return "jdbc:h2:mem:h2mem-" + UUID.randomUUID().toString();
    }

    @Override
    default DBMemoryProvider dbProvider() {
        return this;
    }

}
