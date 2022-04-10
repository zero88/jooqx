package io.github.zero88.jooqx.provider;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.github.zero88.jooqx.LegacyJooqx;
import io.github.zero88.jooqx.LegacySQLCollector;
import io.github.zero88.jooqx.LegacySQLPreparedQuery;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;

/**
 * Legacy {@code jOOQx} provider
 *
 * @since 2.0.0
 */
public interface LegacyJooqxProvider extends
                                     BaseJooqxProvider<SQLClient, JsonArray, LegacySQLPreparedQuery, ResultSet,
                                                          LegacySQLCollector, LegacyJooqx> {

    @Override
    default @NotNull LegacyJooqx createExecutor(Vertx vertx, DSLContext dsl, SQLClient sqlClient) {
        return LegacyJooqx.builder()
                          .setVertx(vertx)
                          .setDSL(dsl)
                          .setSqlClient(sqlClient)
                          .setPreparedQuery(createPreparedQuery())
                          .setResultCollector(createResultCollector())
                          .setErrorConverter(errorConverter())
                          .setTypeMapperRegistry(typeMapperRegistry())
                          .build();
    }

}
