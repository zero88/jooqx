package io.github.zero88.jooq.vertx;

import io.github.zero88.jooq.vertx.adapter.SQLResultAdapter;
import io.github.zero88.jooq.vertx.converter.LegacySQLResultConverter;
import io.vertx.ext.sql.ResultSet;

import lombok.NonNull;

/**
 * Vertx DSL Adapter for Vertx legacy {@code SQL client}
 *
 * @see LegacySQLResultConverter
 * @see SQLResultAdapter
 * @since 1.0.0
 */
public interface VertxLegacyDSL extends VertxDSL<ResultSet, LegacySQLResultConverter> {

    static @NonNull VertxLegacyDSL instance() {
        return new VertxLegacyDSLImpl();
    }

    static @NonNull VertxLegacyDSL create(@NonNull LegacySQLResultConverter converter) {
        return new VertxLegacyDSLImpl(converter);
    }

}
