package io.zero88.jooqx.provider;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.zero88.jooqx.LegacyJooqx;
import io.zero88.jooqx.LegacySQLCollector;
import io.zero88.jooqx.LegacySQLPreparedQuery;

/**
 * Legacy {@code jOOQx} provider
 *
 * @since 1.1.0
 */
public interface LegacyJooqxProvider extends
                                     BaseJooqxProvider<SQLClient, JsonArray, LegacySQLPreparedQuery, ResultSet,
                                                          LegacySQLCollector, LegacyJooqx> {

    @Override
    default @NotNull LegacyJooqx createExecutor(Vertx vertx, DSLContext dsl, SQLClient sqlClient) {
        return LegacyJooqx.builder()
                          .vertx(vertx)
                          .dsl(dsl)
                          .sqlClient(sqlClient)
                          .preparedQuery(createPreparedQuery())
                          .resultCollector(createResultCollector())
                          .errorConverter(errorConverter())
                          .typeMapperRegistry(typeMapperRegistry())
                          .build();
    }

}
