package io.zero88.jooqx;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

/**
 * Reactive result batch converter
 *
 * @see ReactiveSQLResultCollector
 * @since 1.0.0
 */
public interface ReactiveSQLBatchCollector
    extends ReactiveSQLResultCollector, SQLBatchCollector<RowSet<Row>, RowSet<Row>> {}
