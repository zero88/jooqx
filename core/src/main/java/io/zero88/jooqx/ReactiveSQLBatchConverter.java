package io.zero88.jooqx;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

/**
 * Reactive result batch converter
 *
 * @see ReactiveSQLResultConverter
 * @since 1.0.0
 */
public interface ReactiveSQLBatchConverter
    extends ReactiveSQLResultConverter, SQLBatchConverter<RowSet<Row>, RowSet<Row>> {}
