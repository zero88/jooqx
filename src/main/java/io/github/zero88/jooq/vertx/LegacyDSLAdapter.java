package io.github.zero88.jooq.vertx;

import io.github.zero88.jooq.vertx.LegacySQLImpl.LegacyDSLAI;
import io.github.zero88.jooq.vertx.LegacySQLImpl.LegacySQLRSC;
import io.github.zero88.jooq.vertx.adapter.SQLResultAdapter;
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
