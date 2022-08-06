package io.github.zero88.jooqx;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.jooqx.provider.BaseJooqxFacade;
import io.github.zero88.jooqx.provider.DBProvider;
import io.github.zero88.jooqx.provider.JooqxFacade;
import io.github.zero88.jooqx.provider.JooqxProvider;
import io.github.zero88.jooqx.provider.JooqxSQLClientProvider;
import io.github.zero88.jooqx.provider.LegacyJooqxProvider;
import io.github.zero88.jooqx.provider.LegacySQLClientProvider;
import io.github.zero88.jooqx.provider.SQLClientProvider;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.spi.DataSourceProvider;
import io.vertx.ext.sql.SQLClient;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

/**
 * SQL test interface
 *
 * @see BaseJooqxFacade
 * @see HasDBProvider
 * @see JooqDSLProvider
 * @see LegacySQLTest
 * @see JooqxTest
 */
public interface SQLTest<S, B, PQ extends SQLPreparedQuery<B>, RC extends SQLResultCollector, E extends SQLExecutor<S, B, PQ, RC>, DB, DBP extends DBProvider<DB>>
    extends BaseJooqxFacade<S, B, PQ, RC, E>, HasDBProvider<DB, DBP>, JooqDSLProvider {

    interface LegacySQLTest<K, D extends DBProvider<K>, P extends DataSourceProvider>
        extends SQLTest<SQLClient, JsonArray, LegacySQLPreparedQuery, LegacySQLCollector, LegacyJooqx, K, D>,
                LegacyJooqxProvider, LegacySQLClientProvider<P> {

        @Override
        default @NotNull SQLClientProvider<SQLClient> clientProvider() { return this; }

        @Override
        default @NotNull LegacyJooqxProvider jooqxProvider() { return this; }

    }


    interface JooqxTest<S extends SqlClient, K, D extends DBProvider<K>>
        extends JooqxFacade<S>, JooqxProvider<S>, JooqxSQLClientProvider<S>,
                SQLTest<S, Tuple, JooqxPreparedQuery, JooqxResultCollector, JooqxBase<S>, K, D> {

        @Override
        default @NotNull SQLClientProvider<S> clientProvider() { return this; }

        default @NotNull JooqxProvider<S> jooqxProvider() { return this; }

    }

}
