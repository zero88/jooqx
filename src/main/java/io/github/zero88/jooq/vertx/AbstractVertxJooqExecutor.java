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
public abstract class AbstractVertxJooqExecutor<T, R> implements VertxJooqExecutor<T, R> {

    @NonNull
    private final Vertx vertx;
    @NonNull
    private final DSLContext dsl;
    @Default
    @NonNull
    private final QueryHelper helper = new QueryHelper();
    @NonNull
    private final T sqlClient;

}
