package io.github.zero88.jooq.vertx;

import io.github.zero88.jooq.vertx.converter.LegacyResultSetConverter;
import io.vertx.ext.sql.ResultSet;

import lombok.NonNull;

final class VertxLegacyDSLImpl extends VertxDSLImpl<ResultSet, LegacyResultSetConverter> implements VertxLegacyDSL {

    VertxLegacyDSLImpl(@NonNull LegacyResultSetConverter converter) {
        super(converter);
    }

    VertxLegacyDSLImpl() {
        super(new LegacyResultSetConverter());
    }

}
