package io.github.zero88.jooq.vertx;

import org.jooq.DSLContext;
import org.jooq.Query;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.sql.SQLClient;
import io.vertx.sqlclient.SqlClient;

import lombok.NonNull;

/**
 * @param <S> Type of SQL client. Might be {@link SqlClient} or {@link SQLClient}
 * @param <R> Type of Result
 */
public interface VertxJooqExecutor<S, R> {

    Vertx vertx();

    DSLContext dsl();

    QueryHelper helper();

    S sqlClient();

    <Q extends Query> void execute(@NonNull Q query, @NonNull Handler<AsyncResult<R>> handler);

}
