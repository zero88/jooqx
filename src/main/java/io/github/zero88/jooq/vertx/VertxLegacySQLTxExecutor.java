package io.github.zero88.jooq.vertx;

import java.util.function.Function;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
public final class VertxLegacySQLTxExecutor extends VertxLegacySQLExecutorImpl<SQLConnection>
    implements SQLTxExecutor<SQLConnection, JsonArray, ResultSet, VertxLegacySQLTxExecutor> {

    @Override
    protected VertxLegacySQLTxExecutor withSqlClient(@NonNull SQLConnection sqlClient) {
        return VertxLegacySQLTxExecutor.builder()
                                       .vertx(vertx())
                                       .sqlClient(sqlClient)
                                       .dsl(dsl())
                                       .preparedQuery(preparedQuery())
                                       .errorConverter(errorConverter())
                                       .build();
    }

    @Override
    protected Future<SQLConnection> openConn() {
        return null;
    }

    @Override
    public <X> Future<X> run(@NonNull Function<VertxLegacySQLTxExecutor, Future<X>> function) {
        final Promise<X> promise = Promise.promise();
        openConn().map(conn -> conn.setAutoCommit(false, committable -> {
            if (committable.failed()) {
                promise.fail(errorConverter().handle(connFailed("Unable begin transaction", committable.cause())));
            } else {
                function.apply(withSqlClient(conn)).onSuccess(res -> conn.commit(event -> {
                    if (event.succeeded()) {
                        promise.complete(res);
                    } else {
                        handleRollback(conn, promise, errorConverter().handle(event.cause()));
                    }
                }));
            }
        }));
        return promise.future();
    }

    private <X> void handleRollback(@NonNull SQLConnection conn, @NonNull Promise<X> p, @NonNull RuntimeException t) {
        conn.rollback(rb -> {
            if (!rb.succeeded()) {
                t.addSuppressed(rb.cause());
            }
            p.fail(t);
        });
    }

    @Override
    public @NonNull <E extends SQLExecutor<SQLConnection, JsonArray, ResultSet>> SQLTxExecutor<SQLConnection,
                                                                                                      JsonArray,
                                                                                                      ResultSet, E> transaction() {
        return null;
    }

}
