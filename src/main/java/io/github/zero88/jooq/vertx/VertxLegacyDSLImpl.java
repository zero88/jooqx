package io.github.zero88.jooq.vertx;

import io.github.zero88.jooq.vertx.converter.LegacySQLConverter;
import io.github.zero88.jooq.vertx.converter.LegacySQLResultConverter;
import io.vertx.ext.sql.ResultSet;

import lombok.NonNull;

final class VertxLegacyDSLImpl extends VertxDSLImpl<ResultSet, LegacySQLResultConverter> implements VertxLegacyDSL {

    VertxLegacyDSLImpl(@NonNull LegacySQLResultConverter converter) {
        super(converter);
    }

    VertxLegacyDSLImpl() {
        super(LegacySQLConverter.resultSetConverter());
    }

}
