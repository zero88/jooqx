package io.zero88.jooqx;

import java.util.List;

import io.vertx.ext.sql.ResultSet;

/**
 * Represents for Legacy SQL result set converter
 *
 * @since 1.0.0
 */
public interface LegacySQLConverter
    extends SQLResultConverter<ResultSet>, SQLBatchConverter<ResultSet, List<Integer>> {}
