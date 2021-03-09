package io.github.zero88.jooq.vertx;

import org.jooq.Query;
import org.jooq.TableLike;
import org.jooq.conf.ParamType;

import io.github.zero88.jooq.vertx.adapter.SqlResultAdapter;
import io.github.zero88.jooq.vertx.converter.LegacyBindParamConverter;
import io.github.zero88.jooq.vertx.converter.LegacyResultSetConverter;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;

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
public final class VertxLegacyJdbcExecutor extends AbstractVertxJooqExecutor<SQLClient, JsonArray, ResultSet>
    implements VertxBatchExecutor {

    @NonNull
    @Default
    private final QueryHelper<JsonArray> helper = new QueryHelper<>(new LegacyBindParamConverter());

    @Override
    public <Q extends Query, T extends TableLike<?>, C extends ResultSetConverter<ResultSet>, R> void execute(
        @NonNull Q query, @NonNull SqlResultAdapter<ResultSet, C, T, R> resultAdapter,
        @NonNull Handler<AsyncResult<R>> handler) {
        sqlClient().queryWithParams(helper().toPreparedQuery(dsl().configuration(), query, ParamType.INDEXED),
                                    helper().toBindValues(query), ar -> handler.handle(
                ar.map(resultAdapter::convert).otherwise(errorConverter()::reThrowError)));
    }

    @Override
    public <Q extends Query> void batchExecute(@NonNull Q query, @NonNull BindBatchValues bindBatchValues,
                                               @NonNull Handler<AsyncResult<BatchResult>> handler) {
        sqlClient().getConnection(arc -> {
            if (arc.failed()) {
                throw new IllegalStateException("Unable open SQL connection", arc.cause());
            }
            arc.result()
               .batchWithParams(helper().toPreparedQuery(dsl().configuration(), query, ParamType.INDEXED),
                                helper().toBindValues(query, bindBatchValues), ar -> handler.handle(
                       ar.map(r -> new LegacyResultSetConverter().batchResultSize(r))
                         .map(s -> new BatchResult(bindBatchValues.size(), s))
                         .otherwise(errorConverter()::reThrowError)));
        });
    }

}
