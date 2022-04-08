package io.zero88.jooqx.provider;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.vertx.core.Vertx;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.Jooqx;
import io.zero88.jooqx.JooqxBase;
import io.zero88.jooqx.JooqxConn;
import io.zero88.jooqx.JooqxPreparedQuery;
import io.zero88.jooqx.JooqxResultCollector;

/**
 * Reactive jOOQx provider
 *
 * @param <S> Type of {@link SqlClient}
 * @since 1.1.0
 */
@SuppressWarnings("unchecked")
public interface JooqxProvider<S extends SqlClient>
    extends BaseJooqxProvider<S, Tuple, JooqxPreparedQuery, RowSet<Row>, JooqxResultCollector, JooqxBase<S>> {

    @Override
    default @NotNull JooqxBase<S> createExecutor(Vertx vertx, DSLContext dsl, S sqlClient) {
        if (sqlClient instanceof SqlConnection) {
            return (JooqxBase<S>) JooqxConn.builder()
                                           .setVertx(vertx)
                                           .setDSL(dsl)
                                           .setSqlClient((SqlConnection) sqlClient)
                                           .setPreparedQuery(createPreparedQuery())
                                           .setResultCollector(createResultCollector())
                                           .setErrorConverter(errorConverter())
                                           .setTypeMapperRegistry(typeMapperRegistry())
                                           .build();
        }
        if (sqlClient instanceof Pool) {
            return (JooqxBase<S>) Jooqx.builder()
                                       .setVertx(vertx)
                                       .setDSL(dsl)
                                       .setSqlClient((Pool) sqlClient)
                                       .setPreparedQuery(createPreparedQuery())
                                       .setResultCollector(createResultCollector())
                                       .setErrorConverter(errorConverter())
                                       .setTypeMapperRegistry(typeMapperRegistry())
                                       .build();
        }
        throw new UnsupportedOperationException("Unsupported to SQL client: [" + sqlClient.getClass() + "]");
    }

}
