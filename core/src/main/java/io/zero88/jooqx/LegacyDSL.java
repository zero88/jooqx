package io.zero88.jooqx;

import io.vertx.ext.sql.ResultSet;
import io.zero88.jooqx.LegacySQLImpl.LegacyDSLAdapter;
import io.zero88.jooqx.adapter.SQLResultAdapter;

import lombok.NonNull;

/**
 * DSL for for Vert.x legacy SQL client
 *
 * @see LegacySQLCollector
 * @see SQLResultAdapter
 * @since 1.0.0
 */
public interface LegacyDSL extends DSLAdapter<ResultSet, LegacySQLCollector> {

    static @NonNull LegacyDSL adapter() {
        return new LegacyDSLAdapter();
    }

}
