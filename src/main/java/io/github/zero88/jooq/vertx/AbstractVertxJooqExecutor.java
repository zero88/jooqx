package io.github.zero88.jooq.vertx;

import org.jooq.DSLContext;

import io.vertx.core.Vertx;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
public abstract class AbstractVertxJooqExecutor<S, P, R> implements VertxJooqExecutor<S, P, R> {

    @NonNull
    private final Vertx vertx;
    @NonNull
    private final DSLContext dsl;
    @NonNull
    private final S sqlClient;

}
