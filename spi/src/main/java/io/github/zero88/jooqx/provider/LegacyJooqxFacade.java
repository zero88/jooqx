package io.github.zero88.jooqx.provider;

import io.github.zero88.jooqx.LegacyJooqx;
import io.github.zero88.jooqx.LegacySQLCollector;
import io.github.zero88.jooqx.LegacySQLPreparedQuery;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.SQLClient;

/**
 * Represents for Jooqx Legacy facade
 *
 * @see LegacyJooqx
 * @since 2.0.0
 */
public interface LegacyJooqxFacade
    extends BaseJooqxFacade<SQLClient, JsonArray, LegacySQLPreparedQuery, LegacySQLCollector, LegacyJooqx> {

}
