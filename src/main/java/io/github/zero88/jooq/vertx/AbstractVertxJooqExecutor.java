package io.github.zero88.jooq.vertx;

import org.jooq.DSLContext;

import io.vertx.core.Vertx;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
abstract class AbstractVertxJooqExecutor<S, P, RS> implements VertxJooqExecutor<S, P, RS> {

    @NonNull
    private final Vertx vertx;
    @NonNull
    private final DSLContext dsl;
    @NonNull
    private final S sqlClient;
    @Default
    @NonNull
    private final SqlErrorConverter<? extends Throwable, ? extends RuntimeException> errorConverter
        = SqlErrorConverter.DEFAULT;

}
