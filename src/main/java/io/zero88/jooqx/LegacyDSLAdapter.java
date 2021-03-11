package io.zero88.jooqx;

import io.zero88.jooqx.LegacySQLImpl.LegacyDSLAI;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.vertx.ext.sql.ResultSet;

import lombok.NonNull;

/**
 * Vertx DSL Adapter for Vertx legacy {@code SQL client}
 *
 * @see LegacySQLResultConverter
 * @see SQLResultAdapter
 * @since 1.0.0
 */
public interface LegacyDSLAdapter extends DSLAdapter<ResultSet, LegacySQLResultConverter> {

    static @NonNull LegacyDSLAdapter instance() {
        return new LegacyDSLAI();
    }

    static @NonNull LegacyDSLAdapter create(@NonNull LegacySQLResultConverter converter) {
        return new LegacyDSLAI(converter);
    }

}
