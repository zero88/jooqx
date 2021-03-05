package io.github.zero88.jooq.vertx;

import java.util.List;
import java.util.function.Function;

import org.jooq.Query;
import org.jooq.TableLike;
import org.jooq.conf.ParamType;

import io.github.zero88.jooq.vertx.converter.BindBatchValues;
import io.github.zero88.jooq.vertx.converter.LegacyBindParamConverter;
import io.github.zero88.jooq.vertx.converter.ResultSetBatchConverter;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;

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
public class VertxLegacyJdbcExecutor extends AbstractVertxJooqExecutor<SQLClient, JsonArray, ResultSet>
    implements VertxBatchExecutor<ResultSet, Integer> {

    @NonNull
    @Default
    private final QueryHelper<JsonArray> helper = new QueryHelper<>(new LegacyBindParamConverter());

    protected <Q extends Query, R> void doExecute(Q query, Function<ResultSet, List<R>> converter,
                                                  Handler<AsyncResult<List<R>>> handler) {
        sqlClient().queryWithParams(helper().toPreparedQuery(dsl().configuration(), query, ParamType.INDEXED),
                                    helper().toBindValues(query), ar -> handler.handle(ar.map(converter)));
    }

    @Override
    public <Q extends Query, T extends TableLike<?>> void batchExecute(@NonNull Q query,
                                                                       @NonNull ResultSetBatchConverter<ResultSet, T> rsConverter,
                                                                       @NonNull BindBatchValues bindBatchValues,
                                                                       @NonNull Handler<AsyncResult<Integer>> handler) {
        sqlClient().getConnection(arc -> {
            if (arc.failed()) {
                throw new IllegalStateException("Unable open SQL connection", arc.cause());
            }
            final SQLConnection conn = arc.result();
            conn.batchWithParams(helper().toPreparedQuery(dsl().configuration(), query, ParamType.INDEXED),
                                 helper().toBindValues(query, bindBatchValues), ar -> handler.handle(ar.map(s -> {
                    System.out.println(s);
                    return s.size();
                })));
        });
    }

}
