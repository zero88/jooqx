package io.github.zero88.jooq.vertx;

import java.util.List;
import java.util.function.Function;

import org.jooq.Query;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.adapter.SQLResultAdapter;
import io.github.zero88.jooq.vertx.converter.LegacySQLConverter;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;
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
public final class VertxLegacySQLExecutor extends SQLExecutorImpl<SQLClient, JsonArray, ResultSet>
    implements SQLTxExecutor<SQLClient, JsonArray, ResultSet, VertxLegacySQLExecutor> {

    @Override
    public <Q extends Query, T extends TableLike<?>, C extends ResultSetConverter<ResultSet>, R> Future<R> execute(
        @NonNull Q query, @NonNull SQLResultAdapter<ResultSet, C, T, R> resultAdapter) {
        final Promise<ResultSet> promise = Promise.promise();
        sqlClient().queryWithParams(preparedQuery().sql(dsl().configuration(), query),
                                    preparedQuery().bindValues(query), promise);
        return promise.future().map(resultAdapter::convert).otherwise(errorConverter()::reThrowError);
    }

    @Override
    public <Q extends Query> Future<BatchResult> batch(@NonNull Q query, @NonNull BindBatchValues bindBatchValues) {
        final Promise<List<Integer>> promise = Promise.promise();
        openConn().map(c -> c.batchWithParams(preparedQuery().sql(dsl().configuration(), query),
                                              preparedQuery().bindValues(query, bindBatchValues), promise));
        return promise.future()
                      .map(r -> LegacySQLConverter.resultSetConverter().batchResultSize(r))
                      .map(s -> BatchResultImpl.create(bindBatchValues.size(), s))
                      .otherwise(errorConverter()::reThrowError);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull SQLTxExecutor<SQLClient, JsonArray, ResultSet, VertxLegacySQLExecutor> transaction() {
        return this;
    }

    @Override
    protected VertxLegacySQLExecutor withSqlClient(@NonNull SQLClient sqlClient) {
        return VertxLegacySQLExecutor.builder()
                                     .vertx(vertx())
                                     .sqlClient(sqlClient)
                                     .dsl(dsl())
                                     .preparedQuery(preparedQuery())
                                     .errorConverter(errorConverter())
                                     .build();
    }

    private Future<SQLConnection> openConn() {
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

    @Override
    public <X> Future<X> run(@NonNull Function<VertxLegacySQLExecutor, Future<X>> function) {
        final Promise<X> promise = Promise.promise();
        openConn().map(conn -> conn.setAutoCommit(false, committable -> {
            if (committable.failed()) {
                promise.fail(errorConverter().handle(connFailed("Unable begin transaction", committable.cause())));
            } else {
                function.apply(withSqlClient((SQLClient) conn)).onSuccess(res -> conn.commit(event -> {
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

}
