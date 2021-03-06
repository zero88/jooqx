package io.github.zero88.jooq.vertx;

import java.util.List;

import org.jooq.Query;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.adapter.ListResultAdapter;
import io.github.zero88.jooq.vertx.adapter.SqlResultAdapter;
import io.github.zero88.jooq.vertx.converter.ReactiveBindParamConverter;
import io.github.zero88.jooq.vertx.converter.ReactiveResultBatchConverter;
import io.github.zero88.jooq.vertx.converter.ResultBatchConverter;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;
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
    implements VertxReactiveBatchExecutor {

    @NonNull
    @Default
    private final QueryHelper<Tuple> helper = new QueryHelper<>(new ReactiveBindParamConverter());

    @Override
    public <Q extends Query, T extends TableLike<?>, C extends ResultSetConverter<RowSet<Row>>, R> void execute(
        @NonNull Q query, @NonNull SqlResultAdapter<RowSet<Row>, C, T, R> resultMapper,
        @NonNull Handler<AsyncResult<R>> handler) {
        sqlClient().preparedQuery(helper().toPreparedQuery(dsl().configuration(), query))
                   .execute(helper().toBindValues(query), ar -> handler.handle(ar.map(resultMapper::convert)));
    }

    @Override
    public <Q extends Query> void batchExecute(@NonNull Q query, @NonNull BindBatchValues bindBatchValues,
                                               @NonNull Handler<AsyncResult<BatchResult>> handler) {
        sqlClient().preparedQuery(helper().toPreparedQuery(dsl().configuration(), query))
                   .executeBatch(helper().toBindValues(query, bindBatchValues), ar -> handler.handle(
                       ar.map(r -> new ReactiveResultBatchConverter().count(r))
                         .map(s -> new BatchResult(bindBatchValues.size(), s))));
    }

    @Override
    public <Q extends Query, C extends ResultBatchConverter<RowSet<Row>, RowSet<Row>>, T extends TableLike<?>, R> void batchExecute(
        @NonNull Q query, @NonNull BindBatchValues bindBatchValues,
        @NonNull ListResultAdapter<RowSet<Row>, C, T, R> adapter,
        @NonNull Handler<AsyncResult<BatchReturningResult<R>>> handler) {
        final List<Tuple> args = helper().toBindValues(query, bindBatchValues);
        sqlClient().preparedQuery(helper().toPreparedQuery(dsl().configuration(), query))
                   .executeBatch(args, ar -> handler.handle(
                       ar.map(adapter::convert).map(rs -> new BatchReturningResult<>(bindBatchValues.size(), rs))));
    }

}
