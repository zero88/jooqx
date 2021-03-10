package io.github.zero88.jooq.vertx;

import io.github.zero88.jooq.vertx.converter.ReactiveSQLConverter;
import io.github.zero88.jooq.vertx.converter.ReactiveSQLResultBatchConverter;
import io.github.zero88.jooq.vertx.converter.ReactiveSQLResultConverter;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import lombok.NonNull;

final class VertxReactiveDSLImpl extends VertxDSLImpl<RowSet<Row>, ReactiveSQLResultConverter>
    implements VertxReactiveDSL {

    VertxReactiveDSLImpl(@NonNull ReactiveSQLResultConverter converter) {
        super(converter);
    }

    VertxReactiveDSLImpl() {
        super(ReactiveSQLConverter.resultSetConverter());
    }

}
