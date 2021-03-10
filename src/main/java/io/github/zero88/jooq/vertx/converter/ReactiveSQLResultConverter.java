package io.github.zero88.jooq.vertx.converter;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public interface ReactiveSQLResultConverter extends ResultSetConverter<RowSet<Row>> {}
