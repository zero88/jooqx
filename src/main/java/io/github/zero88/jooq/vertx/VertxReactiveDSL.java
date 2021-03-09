package io.github.zero88.jooq.vertx;

import io.github.zero88.jooq.vertx.converter.ReactiveResultSetConverter;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import lombok.NonNull;

/**
 * Vertx Reactive SQL DSL
 *
 * @see ReactiveResultSetConverter
 * @since 1.0.0
 */
public interface VertxReactiveDSL extends VertxSqlDSL<RowSet<Row>, ReactiveResultSetConverter> {

    static @NonNull VertxReactiveDSL instance() {
        return new VertxReactiveDSLImpl();
    }

    static @NonNull VertxReactiveDSL create(@NonNull ReactiveResultSetConverter converter) {
        return new VertxReactiveDSLImpl(converter);
    }

}
