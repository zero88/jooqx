package io.github.zero88.jooq.vertx;

import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLTransientConnectionException;

import org.jooq.DSLContext;
import org.jooq.exception.SQLStateClass;

import io.vertx.core.Vertx;

import lombok.AccessLevel;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.With;
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
    @With(AccessLevel.PROTECTED)
    private final S sqlClient;
    @Default
    @NonNull
    private final SqlErrorConverter<? extends Throwable, ? extends RuntimeException> errorConverter
        = SqlErrorConverter.DEFAULT;

    protected final RuntimeException connFailed(String errorMsg, Throwable cause) {
        return errorConverter().handle(
            new SQLTransientConnectionException(errorMsg, SQLStateClass.C08_CONNECTION_EXCEPTION.className(), cause));
    }

    protected final RuntimeException connFailed(String errorMsg) {
        return errorConverter().handle(
            new SQLNonTransientConnectionException(errorMsg, SQLStateClass.C08_CONNECTION_EXCEPTION.className()));
    }

}
