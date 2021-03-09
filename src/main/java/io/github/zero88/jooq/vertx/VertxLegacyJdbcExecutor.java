package io.github.zero88.jooq.vertx;

import java.util.List;

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
    public <Q extends Query, T extends TableLike<?>, C extends ResultSetConverter<ResultSet>, R> Future<R> execute(
        @NonNull Q query, @NonNull SqlResultAdapter<ResultSet, C, T, R> resultAdapter) {
        final Promise<ResultSet> promise = Promise.promise();
        sqlClient().queryWithParams(helper().toPreparedQuery(dsl().configuration(), query, ParamType.INDEXED),
                                    helper().toBindValues(query), promise);
        return promise.future().map(resultAdapter::convert).otherwise(errorConverter()::reThrowError);
    }

    @Override
    public <Q extends Query> Future<BatchResult> batchExecute(@NonNull Q query,
                                                              @NonNull BindBatchValues bindBatchValues) {
        final Promise<List<Integer>> promise = Promise.promise();
        sqlClient().getConnection(arc -> {
            if (arc.failed()) {
                promise.fail(new IllegalStateException("Unable open SQL connection", arc.cause()));
            }
            arc.result()
               .batchWithParams(helper().toPreparedQuery(dsl().configuration(), query, ParamType.INDEXED),
                                helper().toBindValues(query, bindBatchValues), promise);
        });
        return promise.future()
                      .map(r -> new LegacyResultSetConverter().batchResultSize(r))
                      .map(s -> new BatchResult(bindBatchValues.size(), s))
                      .otherwise(errorConverter()::reThrowError);
    }

}
