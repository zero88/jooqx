package io.github.zero88.jooq.vertx;

import io.github.zero88.jooq.vertx.adapter.SQLResultAdapter;
import io.github.zero88.jooq.vertx.converter.LegacyResultSetConverter;
import io.vertx.ext.sql.ResultSet;

import lombok.NonNull;

/**
 * Vertx DSL Adapter for Vertx legacy {@code SQL client}
 *
 * @see LegacyResultSetConverter
 * @see SQLResultAdapter
 * @since 1.0.0
 */
public interface VertxLegacyDSL extends VertxDSL<ResultSet, LegacyResultSetConverter> {

    static @NonNull VertxLegacyDSL instance() {
        return new VertxLegacyDSLImpl();
    }

    static @NonNull VertxLegacyDSL create(@NonNull LegacyResultSetConverter converter) {
        return new VertxLegacyDSLImpl(converter);
    }

}
