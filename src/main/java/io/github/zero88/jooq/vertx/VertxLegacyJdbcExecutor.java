package io.github.zero88.jooq.vertx;

import java.util.List;
import java.util.function.Function;

import org.jooq.Query;
import org.jooq.TableLike;
import org.jooq.conf.ParamType;

import io.github.zero88.jooq.vertx.converter.LegacyBindParamConverter;
import io.github.zero88.jooq.vertx.converter.ResultBatchConverter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx legacy JDBC client} connection
 *
 * @see SQLClient
 * @see JDBCClient
 * @see ResultSet
 * @since 1.0.0
 */
@Getter
@SuperBuilder
@Accessors(fluent = true)
public final class VertxLegacyJdbcExecutor
    extends AbstractVertxJooqExecutor<SQLClient, JsonArray, ResultSet, BatchResult> {

    @NonNull
    @Default
    private final QueryHelper<JsonArray> helper = new QueryHelper<>(new LegacyBindParamConverter());

    protected <Q extends Query, R> void doExecute(Q query, Function<ResultSet, List<R>> converter,
                                                  Handler<AsyncResult<List<R>>> handler) {
        sqlClient().queryWithParams(helper().toPreparedQuery(dsl().configuration(), query, ParamType.INDEXED),
                                    helper().toBindValues(query), ar -> handler.handle(ar.map(converter)));
    }

    @Override
    public <Q extends Query, T extends TableLike<?>> void batchExecute(@NonNull Q query,
                                                                       @NonNull BindBatchValues bindBatchValues,
                                                                       @NonNull ResultBatchConverter<ResultSet, T> rsConverter,
                                                                       @NonNull Handler<AsyncResult<BatchResult>> handler) {
        sqlClient().getConnection(arc -> {
            if (arc.failed()) {
                throw new IllegalStateException("Unable open SQL connection", arc.cause());
            }
            doBatchExecute(arc.result(), query, bindBatchValues, handler);
        });
    }

    private <Q extends Query> void doBatchExecute(@NonNull SQLConnection c, @NonNull Q q, @NonNull BindBatchValues b,
                                                  @NonNull Handler<AsyncResult<BatchResult>> h) {
        c.batchWithParams(helper().toPreparedQuery(dsl().configuration(), q, ParamType.INDEXED),
                          helper().toBindValues(q, b),
                          ar -> h.handle(ar.map(ar.result().size()).map(s -> new BatchResult(b.size(), s))));
    }

}
