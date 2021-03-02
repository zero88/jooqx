package io.github.zero88.jooq.vertx;

import java.util.ArrayList;
import java.util.List;

import org.jooq.Query;
import org.jooq.TableLike;
import org.jooq.conf.ParamType;

import io.github.zero88.jooq.vertx.converter.LegacyParamConverter;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;
import io.github.zero88.jooq.vertx.record.VertxJooqRecord;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
public class VertxLegacyJdbcExecutor extends AbstractVertxJooqExecutor<SQLClient, JsonArray, ResultSet> {

    @NonNull
    @Default
    private final QueryHelper<JsonArray> helper = new QueryHelper<>(new LegacyParamConverter());

    @Override
    public <Q extends Query, T extends TableLike<?>> void execute(@NonNull Q query,
                                                                  @NonNull ResultSetConverter<ResultSet, T> rsConverter,
                                                                  @NonNull Handler<AsyncResult<List<VertxJooqRecord<?>>>> handler) {
        sqlClient().queryWithParams(helper().toPreparedQuery(dsl().configuration(), query, ParamType.INDEXED),
                                    helper().toBindValues(query), ar -> handler.handle(ar.map(rsConverter::convert)));
    }

    public <Q extends Query> void batchExecute(@NonNull Q query, @NonNull Handler<AsyncResult<List<Integer>>> handler) {
        sqlClient().getConnection(ar -> {
            final SQLConnection conn = ar.result();
            conn.batchWithParams(helper().toPreparedQuery(dsl().configuration(), query, ParamType.INDEXED),
                                 new ArrayList<>(), handler);
        });
    }

}
