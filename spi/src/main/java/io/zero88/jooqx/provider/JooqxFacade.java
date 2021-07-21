package io.zero88.jooqx.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.JooqDSLProvider;
import io.zero88.jooqx.LegacyJooqx;
import io.zero88.jooqx.LegacySQLCollector;
import io.zero88.jooqx.LegacySQLPreparedQuery;
import io.zero88.jooqx.ReactiveJooqx;
import io.zero88.jooqx.ReactiveJooqxBase;
import io.zero88.jooqx.ReactiveJooqxConn;
import io.zero88.jooqx.ReactiveSQLPreparedQuery;
import io.zero88.jooqx.ReactiveSQLResultCollector;
import io.zero88.jooqx.SQLExecutor;
import io.zero88.jooqx.SQLPreparedQuery;
import io.zero88.jooqx.SQLResultCollector;

/**
 * A {@code jOOQ.x} facade to help initialize new {@code jOOQ.x} instance
 *
 * @param <S>  Type of Vertx SQL client
 * @param <B>  Type of Vertx SQL bind value holder
 * @param <RS> Type of Vertx SQL result set holder
 * @param <PQ> Type of SQL prepare query
 * @param <RC> Type of SQL result set collector
 * @param <E>  Type of jOOQ.x
 * @see JooqxLegacyFacade
 * @see JooqxReactiveFacade
 * @since 1.1.0
 */
public interface JooqxFacade<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>,
                                E extends SQLExecutor<S, B, PQ, RS, RC>> {

    @NotNull SQLClientProvider<S> clientProvider();

    @NotNull JooqxProvider<S, B, PQ, RS, RC, E> jooqxProvider();

    /**
     * Init {@code jOOQ.x} instance from configuration
     *
     * @param vertx       vertx
     * @param dslProvider jOOQ DSL provider
     * @param connOptions SQL connection options
     * @param poolOptions SQL pool options
     * @return {@code jOOQ.x} instance
     */
    default Future<E> jooqx(Vertx vertx, JooqDSLProvider dslProvider, JsonObject connOptions,
                            @Nullable JsonObject poolOptions) {
        return jooqx(vertx, dslProvider.dsl(), connOptions, poolOptions);
    }

    /**
     * Init {@code jOOQ.x} instance from configuration
     *
     * @param vertx       vertx
     * @param dsl         jOOQ DSL context
     * @param connOptions SQL connection options
     * @param poolOptions SQL pool options
     * @return {@code jOOQ.x} instance
     */
    default Future<E> jooqx(Vertx vertx, DSLContext dsl, JsonObject connOptions, @Nullable JsonObject poolOptions) {
        return clientProvider().open(vertx, connOptions, poolOptions)
                               .map(sqlClient -> jooqxProvider().createExecutor(vertx, dsl, sqlClient));
    }

    /**
     * Represents for Jooqx Reactive facade
     *
     * @param <S> Type of reactive SQL client
     * @see ReactiveJooqxBase
     * @see ReactiveJooqx
     * @see ReactiveJooqxConn
     * @see SqlClient
     * @since 1.1.0
     */
    interface JooqxReactiveFacade<S extends SqlClient> extends
                                                       JooqxFacade<S, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>,
                                                                      ReactiveSQLResultCollector,
                                                                      ReactiveJooqxBase<S>> {

    }


    /**
     * Represents for Jooqx Legacy facade
     *
     * @see LegacyJooqx
     * @since 1.1.0
     */
    interface JooqxLegacyFacade
        extends JooqxFacade<SQLClient, JsonArray, LegacySQLPreparedQuery, ResultSet, LegacySQLCollector, LegacyJooqx> {

    }

}
