package io.zero88.jooqx.provider;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.zero88.jooqx.LegacyJooqx;
import io.zero88.jooqx.LegacySQLCollector;
import io.zero88.jooqx.LegacySQLPreparedQuery;

/**
 * Legacy jOOQx provider
 */
public interface LegacyJooqxProvider
    extends JooqxProvider<SQLClient, JsonArray, LegacySQLPreparedQuery, ResultSet, LegacySQLCollector, LegacyJooqx> {

    @Override
    default LegacyJooqx createExecutor(Vertx vertx, JooqDSLProvider dslProvider, SQLClient sqlClient) {
        return LegacyJooqx.builder()
                          .vertx(vertx)
                          .dsl(dslProvider.dsl())
                          .sqlClient(sqlClient)
                          .preparedQuery(createPreparedQuery())
                          .resultCollector(createResultCollector())
                          .errorConverter(errorConverter())
                          .typeMapperRegistry(typeMapperRegistry())
                          .build();
    }

}
