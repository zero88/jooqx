package io.zero88.jooqx;

import org.jetbrains.annotations.NotNull;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.spi.DataSourceProvider;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.provider.DBProvider;
import io.zero88.jooqx.provider.JooqxFacade;
import io.zero88.jooqx.provider.LegacyJooqxProvider;
import io.zero88.jooqx.provider.LegacySQLClientProvider;
import io.zero88.jooqx.provider.ReactiveJooqxProvider;
import io.zero88.jooqx.provider.ReactiveSQLClientProvider;
import io.zero88.jooqx.provider.SQLClientProvider;

/**
 * SQL test interface
 *
 * @see JooqxFacade
 * @see HasDBProvider
 * @see JooqDSLProvider
 * @see LegacySQLTest
 * @see ReactiveSQLTest
 */
public interface SQLTest<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>,
                            E extends SQLExecutor<S, B, PQ, RS, RC>, DB, DBP extends DBProvider<DB>>
    extends JooqxFacade<S, B, PQ, RS, RC, E>, HasDBProvider<DB, DBP>, JooqDSLProvider {

    interface LegacySQLTest<K, D extends DBProvider<K>, P extends DataSourceProvider>
        extends SQLTest<SQLClient, JsonArray, LegacySQLPreparedQuery, ResultSet, LegacySQLCollector, LegacyJooqx, K, D>,
                LegacyJooqxProvider, LegacySQLClientProvider<P> {

        @Override
        default @NotNull SQLClientProvider<SQLClient> clientProvider() { return this; }

        @Override
        default @NotNull LegacyJooqxProvider jooqxProvider() { return this; }

    }


    interface ReactiveSQLTest<S extends SqlClient, K, D extends DBProvider<K>>
        extends JooqxReactiveFacade<S>, ReactiveJooqxProvider<S>, ReactiveSQLClientProvider<S>,
                SQLTest<S, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>, ReactiveSQLResultCollector,
                           ReactiveJooqxBase<S>, K, D> {

        @Override
        default @NotNull SQLClientProvider<S> clientProvider() { return this; }

        default @NotNull ReactiveJooqxProvider<S> jooqxProvider() { return this; }

    }

}
