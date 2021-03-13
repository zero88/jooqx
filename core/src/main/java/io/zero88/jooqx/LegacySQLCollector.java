package io.zero88.jooqx;

import java.util.List;

import io.vertx.ext.sql.ResultSet;

/**
 * Represents for Legacy SQL result set collector
 *
 * @since 1.0.0
 */
public interface LegacySQLCollector
    extends SQLResultCollector<ResultSet>, SQLBatchCollector<ResultSet, List<Integer>> {}
