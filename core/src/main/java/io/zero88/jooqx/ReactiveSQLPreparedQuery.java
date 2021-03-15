package io.zero88.jooqx;

import io.vertx.sqlclient.Tuple;

/**
 * Represents for a converter that transforms {@code jOOQ param} to {@code Vertx Reactive SQL} bind value
 *
 * @since 1.0.0
 */
public interface ReactiveSQLPreparedQuery extends SQLPreparedQuery<Tuple> {}
