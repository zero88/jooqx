package io.zero88.jooqx;

import io.vertx.ext.sql.ResultSet;
import io.zero88.jooqx.LegacySQLImpl.LegacyDSLAI;
import io.zero88.jooqx.adapter.SQLResultAdapter;

import lombok.NonNull;

/**
 * Vertx DSL Adapter for Vertx legacy {@code SQL client}
 *
 * @see LegacySQLConverter
 * @see SQLResultAdapter
 * @since 1.0.0
 */
public interface LegacyDSL extends DSLAdapter<ResultSet, LegacySQLConverter> {

    static @NonNull LegacyDSL adapter() {
        return new LegacyDSLAI();
    }

    static @NonNull LegacyDSL create(@NonNull LegacySQLConverter converter) {
        return new LegacyDSLAI(converter);
    }

}
