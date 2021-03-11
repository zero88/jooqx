package io.zero88.jooqx;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public interface ReactiveSQLResultConverter extends SQLResultSetConverter<RowSet<Row>> {}
