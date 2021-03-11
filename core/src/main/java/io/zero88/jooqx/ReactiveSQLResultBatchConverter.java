package io.zero88.jooqx;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

public interface ReactiveSQLResultBatchConverter extends SQLResultBatchConverter<RowSet<Row>, RowSet<Row>> {}
