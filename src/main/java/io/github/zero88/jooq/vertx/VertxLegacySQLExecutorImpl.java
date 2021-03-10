package io.github.zero88.jooq.vertx;

import java.util.List;

import org.jooq.Query;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.adapter.SQLResultAdapter;
import io.github.zero88.jooq.vertx.converter.LegacySQLConverter;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.sql.SQLOperations;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
abstract class VertxLegacySQLExecutorImpl<S extends SQLOperations> extends SQLExecutorImpl<S, JsonArray, ResultSet> {

    @Override
    public final <Q extends Query, T extends TableLike<?>, C extends ResultSetConverter<ResultSet>, R> Future<R> execute(
        @NonNull Q query, @NonNull SQLResultAdapter<ResultSet, C, T, R> resultAdapter) {
        final Promise<ResultSet> promise = Promise.promise();
        sqlClient().queryWithParams(preparedQuery().sql(dsl().configuration(), query),
                                    preparedQuery().bindValues(query), promise);
        return promise.future().map(resultAdapter::convert).otherwise(errorConverter()::reThrowError);
    }

    @Override
    public final <Q extends Query> Future<BatchResult> batch(@NonNull Q query,
                                                             @NonNull BindBatchValues bindBatchValues) {
        final Promise<List<Integer>> promise = Promise.promise();
        openConn().map(c -> c.batchWithParams(preparedQuery().sql(dsl().configuration(), query),
                                              preparedQuery().bindValues(query, bindBatchValues), promise));
        return promise.future()
                      .map(r -> LegacySQLConverter.resultSetConverter().batchResultSize(r))
                      .map(s -> BatchResultImpl.create(bindBatchValues.size(), s))
                      .otherwise(errorConverter()::reThrowError);
    }

    protected abstract Future<SQLConnection> openConn();

}
