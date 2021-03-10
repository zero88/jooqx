package io.github.zero88.jooq.vertx.converter;

import java.util.List;

import io.vertx.ext.sql.ResultSet;

public interface LegacySQLResultConverter
    extends ResultSetConverter<ResultSet>, ResultBatchConverter<ResultSet, List<Integer>> {}
