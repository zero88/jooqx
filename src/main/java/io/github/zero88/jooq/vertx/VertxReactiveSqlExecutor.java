package io.github.zero88.jooq.vertx;

import org.jooq.Query;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class VertxReactiveSqlExecutor extends AbstractVertxJooqExecutor<SqlClient, RowSet<Row>> {

    public <T extends Query> void execute(@NonNull T query, @NonNull Handler<AsyncResult<RowSet<Row>>> handler) {
        sqlClient().preparedQuery(helper().toPreparedQuery(dsl().configuration(), query))
                   .execute(helper().getBindValues(query), handler);
    }

    public <T extends Query> void batchExecute(@NonNull T query, @NonNull Handler<AsyncResult<RowSet<Row>>> handler) {
        final String sql = helper().toPreparedQuery(dsl().configuration(), query);
        final Tuple params = helper().getBindValues(query);
        sqlClient().preparedQuery(sql).execute(params, handler);
    }

}
