package io.github.zero88.jooq.vertx;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public interface ReactiveSQLResultConverter extends SQLResultSetConverter<RowSet<Row>> {}
