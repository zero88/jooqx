package io.zero88.jooqx;

import java.util.function.Function;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.SQLOperations;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx legacy JDBC client} connection
 *
 * @see SQLClient
 * @see SQLConnection
 * @see SQLOperations
 * @see JDBCClient
 * @see ResultSet
 * @since 1.0.0
 */
@Getter
@SuperBuilder
@Accessors(fluent = true)
public final class LegacyJooqx extends LegacySQLImpl.LegacySQLEI<SQLClient> {

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull SQLTxExecutor<SQLConnection, JsonArray, ResultSet, LegacySQLTxExecutor> transaction() {
        return LegacySQLTxExecutor.builder()
                                  .vertx(vertx())
                                  .dsl(dsl())
                                  .preparedQuery(preparedQuery())
                                  .errorConverter(errorConverter())
                                  .delegate(this)
                                  .build();
    }

    @Override
    protected LegacyJooqx withSqlClient(@NonNull SQLClient sqlClient) {
        throw new UnsupportedOperationException("No need");
    }

    protected Future<SQLConnection> openConn() {
        final Promise<SQLConnection> promise = Promise.promise();
        sqlClient().getConnection(ar -> {
            if (ar.failed()) {
                promise.fail(connFailed("Unable open SQL connection", ar.cause()));
            } else {
                promise.complete(ar.result());
            }
        });
        return promise.future();
    }

    @Getter
    @SuperBuilder
    @Accessors(fluent = true)
    public static final class LegacySQLTxExecutor extends LegacySQLImpl.LegacySQLEI<SQLConnection>
        implements SQLTxExecutor<SQLConnection, JsonArray, ResultSet, LegacySQLTxExecutor> {

        private final LegacySQLImpl.LegacySQLEI<SQLClient> delegate;

        @Override
        public <X> Future<X> run(@NonNull Function<LegacySQLTxExecutor, Future<X>> block) {
            final Promise<X> promise = Promise.promise();
            delegate.openConn().map(conn -> conn.setAutoCommit(false, committable -> {
                if (committable.failed()) {
                    failed(conn, promise, connFailed("Unable begin transaction", committable.cause()));
                } else {
                    block.apply(withSqlClient(conn))
                         .onSuccess(r -> commit(conn, promise, r))
                         .onFailure(t -> rollback(conn, promise, t));
                }
            }));
            return promise.future();
        }

        @Override
        @SuppressWarnings("unchecked")
        public @NonNull LegacyJooqx.LegacySQLTxExecutor transaction() {
            return this;
        }

        @Override
        protected Future<SQLConnection> openConn() {
            return Future.succeededFuture(sqlClient());
        }

        @Override
        protected LegacySQLTxExecutor withSqlClient(@NonNull SQLConnection sqlConn) {
            return LegacySQLTxExecutor.builder()
                                      .vertx(vertx())
                                      .sqlClient(sqlConn)
                                      .dsl(dsl())
                                      .preparedQuery(preparedQuery())
                                      .errorConverter(errorConverter())
                                      .build();
        }

        private <X> void commit(@NonNull SQLConnection conn, @NonNull Promise<X> promise, X output) {
            conn.commit(v -> {
                if (v.succeeded()) {
                    promise.complete(output);
                    conn.close();
                } else {
                    rollback(conn, promise, errorConverter().handle(v.cause()));
                }
            });
        }

        private <X> void rollback(@NonNull SQLConnection conn, @NonNull Promise<X> promise, @NonNull Throwable t) {
            conn.rollback(rb -> {
                if (!rb.succeeded()) {
                    t.addSuppressed(rb.cause());
                }
                failed(conn, promise, t);
            });
        }

        private <X> void failed(@NonNull SQLConnection conn, @NonNull Promise<X> promise, @NonNull Throwable t) {
            promise.fail(t);
            conn.close();
        }

    }

}
