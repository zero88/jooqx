package io.github.zero88.jooq.vertx;

import java.util.List;
import java.util.function.Function;

import org.jooq.Query;

import io.github.zero88.jooq.vertx.converter.SqlTupleParamConverter;
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
public class VertxReactiveSqlExecutor extends AbstractVertxJooqExecutor<SqlClient, Tuple, RowSet<Row>> {

    @NonNull
    @Default
    private final QueryHelper<Tuple> helper = new QueryHelper<>(new SqlTupleParamConverter());

    @Override
    protected <Q extends Query, R> void doExecute(@NonNull Q query, @NonNull Function<RowSet<Row>, List<R>> converter,
                                                  @NonNull Handler<AsyncResult<List<R>>> handler) {
        sqlClient().preparedQuery(helper().toPreparedQuery(dsl().configuration(), query))
                   .execute(helper().toBindValues(query), ar -> handler.handle(ar.map(converter)));
    }

    public <T extends Query> void batchExecute(@NonNull T query, @NonNull Handler<AsyncResult<RowSet<Row>>> handler) {
        final String sql = helper().toPreparedQuery(dsl().configuration(), query);
        final Tuple params = helper().toBindValues(query);
        sqlClient().preparedQuery(sql).execute(params, handler);
    }

}
