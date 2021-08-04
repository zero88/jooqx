package io.zero88.jooqx.provider;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.zero88.jooqx.LegacyJooqx;
import io.zero88.jooqx.LegacySQLCollector;
import io.zero88.jooqx.LegacySQLPreparedQuery;

/**
 * Represents for Jooqx Legacy facade
 *
 * @see LegacyJooqx
 * @since 1.1.0
 */
public interface LegacyJooqxFacade
    extends BaseJooqxFacade<SQLClient, JsonArray, LegacySQLPreparedQuery, ResultSet, LegacySQLCollector, LegacyJooqx> {

}
