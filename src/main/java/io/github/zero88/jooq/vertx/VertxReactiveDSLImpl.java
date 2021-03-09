package io.github.zero88.jooq.vertx;

import io.github.zero88.jooq.vertx.converter.ReactiveResultSetConverter;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import lombok.NonNull;

final class VertxReactiveDSLImpl extends VertxSqlDSLImpl<RowSet<Row>, ReactiveResultSetConverter>
    implements VertxReactiveDSL {

    VertxReactiveDSLImpl(@NonNull ReactiveResultSetConverter converter) {
        super(converter);
    }

    VertxReactiveDSLImpl() {
        super(new ReactiveResultSetConverter());
    }

}
