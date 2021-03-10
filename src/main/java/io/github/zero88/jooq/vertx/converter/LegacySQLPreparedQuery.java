package io.github.zero88.jooq.vertx.converter;

import io.vertx.core.json.JsonArray;

/**
 * Represents for a converter that transforms {@code jOOQ param} to {@code Vertx legacy SQL} bind value
 *
 * @see SQLPreparedQuery
 * @since 1.0.0
 */
public interface LegacySQLPreparedQuery extends SQLPreparedQuery<JsonArray> {}
