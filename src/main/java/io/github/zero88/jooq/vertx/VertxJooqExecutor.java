package io.github.zero88.jooq.vertx;

import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.converter.ResultSetConverter;
import io.github.zero88.jooq.vertx.record.VertxJooqRecord;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.sql.SQLClient;
import io.vertx.sqlclient.SqlClient;

import lombok.NonNull;

/**
 * @param <S> Type of SQL client. Might be {@link SqlClient} or {@link SQLClient}
 * @param <P> Type of SQL bind value holder
 * @param <R> Type of SQL Result set holder
 */
public interface VertxJooqExecutor<S, P, R> {

    @NonNull Vertx vertx();

    @NonNull DSLContext dsl();

    @NonNull QueryHelper<P> helper();

    @NonNull S sqlClient();

    <Q extends Query, T extends TableLike<?>> void execute(@NonNull Q query,
                                                           @NonNull ResultSetConverter<R, T> rsConverter,
                                                           @NonNull Handler<AsyncResult<List<VertxJooqRecord<?>>>> handler);

}
