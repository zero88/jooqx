package io.zero88.jooqx;

import io.vertx.core.json.JsonArray;

/**
 * Represents for a converter that transforms {@code jOOQ param} to {@code Vertx legacy SQL} bind value
 *
 * @since 1.0.0
 */
public interface LegacySQLPreparedQuery extends SQLPreparedQuery<JsonArray> {}
