package io.github.zero88.jooq.vertx;

import io.github.zero88.jooq.vertx.converter.LegacyResultSetConverter;
import io.vertx.ext.sql.ResultSet;

import lombok.NonNull;

public interface VertxLegacyDSL extends VertxSqlDSL<ResultSet, LegacyResultSetConverter> {

    static @NonNull VertxLegacyDSL instance() {
        return new VertxLegacyDSLImpl();
    }

    static @NonNull VertxLegacyDSL create(@NonNull LegacyResultSetConverter converter) {
        return new VertxLegacyDSLImpl(converter);
    }
}
