package io.github.zero88.jooq.vertx;

import java.util.ArrayList;
import java.util.List;

import org.jooq.Query;
import org.jooq.conf.ParamType;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;

import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class VertxLegacyJdbcExecutor extends AbstractVertxJooqExecutor<SQLClient, ResultSet> {

    public <Q extends Query> void execute(@NonNull Q query, @NonNull Handler<AsyncResult<ResultSet>> handler) {
        sqlClient().queryWithParams(helper().toPreparedQuery(dsl().configuration(), query, ParamType.INDEXED),
                                    helper().toBindValues(query), handler);
    }

    public <Q extends Query> void batchExecute(@NonNull Q query, @NonNull Handler<AsyncResult<List<Integer>>> handler) {
        sqlClient().getConnection(ar -> {
            final SQLConnection conn = ar.result();
            conn.batchWithParams(helper().toPreparedQuery(dsl().configuration(), query, ParamType.INDEXED),
                                 new ArrayList<>(), handler);
        });
    }

}
