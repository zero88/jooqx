package io.github.zero88.jooq.vertx;

import org.jooq.Query;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.converter.ReactiveBindParamConverter;
import io.github.zero88.jooq.vertx.adapter.ListResultAdapter;
import io.github.zero88.jooq.vertx.adapter.SqlResultAdapter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

/**
 * Represents for an executor that executes {@code jOOQ query} on {@code Vertx reactive SQL client} connection
 *
 * @see SqlClient
 * @see Tuple
 * @see Row
 * @see RowSet
 * @since 1.0.0
 */
@Getter
@SuperBuilder
@Accessors(fluent = true)
public final class VertxReactiveSqlExecutor extends AbstractVertxJooqExecutor<SqlClient, Tuple, RowSet<Row>>
    implements BatchReactiveSqlExecutor {

    @NonNull
    @Default
    private final QueryHelper<Tuple> helper = new QueryHelper<>(new ReactiveBindParamConverter());

    @Override
    public <Q extends Query, T extends TableLike<?>, R> void execute(@NonNull Q query,
                                                                     @NonNull SqlResultAdapter<RowSet<Row>, T, R> resultMapper,
                                                                     @NonNull Handler<AsyncResult<R>> handler) {
        sqlClient().preparedQuery(helper().toPreparedQuery(dsl().configuration(), query))
                   .execute(helper().toBindValues(query), ar -> handler.handle(ar.map(resultMapper::convert)));
    }

    @Override
    public <Q extends Query, T extends TableLike<?>, R> void batchExecute(@NonNull Q query,
                                                                          @NonNull BindBatchValues bindBatchValues,
                                                                          @NonNull ListResultAdapter<RowSet<Row>, T,
                                                                                                                                                                                 R> resultAdapter,
                                                                          @NonNull Handler<AsyncResult<BatchReturningResult<R>>> handler) {
        sqlClient().preparedQuery(helper().toPreparedQuery(dsl().configuration(), query))
                   .executeBatch(helper().toBindValues(query, bindBatchValues), ar -> handler.handle(
                       ar.map(resultAdapter::convert)
                         .map(rs -> new BatchReturningResult<>(bindBatchValues.size(), rs))));
    }

}
