package io.github.zero88.jooq.vertx;

import java.util.List;

import org.jooq.Query;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.converter.ResultSetConverter;
import io.github.zero88.jooq.vertx.converter.SqlTupleParamConverter;
import io.github.zero88.jooq.vertx.record.VertxJooqRecord;
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

@Getter
@SuperBuilder
@Accessors(fluent = true)
public class VertxReactiveSqlExecutor extends AbstractVertxJooqExecutor<SqlClient, Tuple, RowSet<Row>> {

    @NonNull
    @Default
    private final QueryHelper<Tuple> helper = new QueryHelper<>(new SqlTupleParamConverter());

    @Override
    public <Q extends Query, T extends TableLike<?>> void execute(@NonNull Q query,
                                                                  @NonNull ResultSetConverter<RowSet<Row>, T> rsConverter,
                                                                  @NonNull Handler<AsyncResult<List<VertxJooqRecord<?>>>> handler) {
        sqlClient().preparedQuery(helper().toPreparedQuery(dsl().configuration(), query))
                   .execute(helper().toBindValues(query), ar -> handler.handle(ar.map(rsConverter::convert)));
    }

    public <T extends Query> void batchExecute(@NonNull T query, @NonNull Handler<AsyncResult<RowSet<Row>>> handler) {
        final String sql = helper().toPreparedQuery(dsl().configuration(), query);
        final Tuple params = helper().toBindValues(query);
        sqlClient().preparedQuery(sql).execute(params, handler);
    }

}
