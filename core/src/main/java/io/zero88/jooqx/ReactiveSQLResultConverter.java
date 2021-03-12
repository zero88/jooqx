package io.zero88.jooqx;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

/**
 * Reactive result set converter
 *
 * @since 1.0.0
 */
public interface ReactiveSQLResultConverter extends SQLResultConverter<RowSet<Row>> {}
