package io.github.zero88.jooq.vertx;

import java.util.List;
import java.util.function.Function;

import org.jooq.Query;
import org.jooq.TableLike;
import org.jooq.conf.ParamType;

import io.github.zero88.jooq.vertx.adapter.SqlResultAdapter;
import io.github.zero88.jooq.vertx.converter.LegacyBindParamConverter;
import io.github.zero88.jooq.vertx.converter.LegacyResultSetConverter;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.SQLOperations;

import lombok.Builder.Default;
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
public final class VertxLegacyJdbcExecutor extends AbstractVertxJooqExecutor<SQLClient, JsonArray, ResultSet>
    implements VertxTxExecutor<SQLClient, JsonArray, ResultSet, VertxLegacyJdbcExecutor> {

    @NonNull
    @Default
    private final QueryHelper<JsonArray> helper = new QueryHelper<>(new LegacyBindParamConverter());

    @Override
    public <Q extends Query, T extends TableLike<?>, C extends ResultSetConverter<ResultSet>, R> Future<R> execute(
        @NonNull Q query, @NonNull SqlResultAdapter<ResultSet, C, T, R> resultAdapter) {
        final Promise<ResultSet> promise = Promise.promise();
        sqlClient().queryWithParams(helper().toPreparedQuery(dsl().configuration(), query, ParamType.INDEXED),
                                    helper().toBindValues(query), promise);
        return promise.future().map(resultAdapter::convert).otherwise(errorConverter()::reThrowError);
    }

    @Override
    public <Q extends Query> Future<BatchResult> batch(@NonNull Q query, @NonNull BindBatchValues bindBatchValues) {
        final Promise<List<Integer>> promise = Promise.promise();
        openConn().map(c -> c.batchWithParams(helper().toPreparedQuery(dsl().configuration(), query, ParamType.INDEXED),
                                              helper().toBindValues(query, bindBatchValues), promise));
        return promise.future()
                      .map(r -> new LegacyResultSetConverter().batchResultSize(r))
                      .map(s -> new BatchResult(bindBatchValues.size(), s))
                      .otherwise(errorConverter()::reThrowError);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull VertxTxExecutor<SQLClient, JsonArray, ResultSet, VertxLegacyJdbcExecutor> transaction() {
        return this;
    }

    @Override
    protected VertxLegacyJdbcExecutor withSqlClient(@NonNull SQLClient sqlClient) {
        return VertxLegacyJdbcExecutor.builder()
                                      .vertx(vertx())
                                      .sqlClient(sqlClient)
                                      .dsl(dsl())
                                      .helper(helper())
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
    public <X> Future<X> run(@NonNull Function<VertxLegacyJdbcExecutor, Future<X>> function) {
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
