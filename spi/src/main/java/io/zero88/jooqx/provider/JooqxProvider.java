package io.zero88.jooqx.provider;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.vertx.core.Vertx;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.JooqxBase;
import io.zero88.jooqx.JooqxPreparedQuery;
import io.zero88.jooqx.JooqxResultCollector;

/**
 * Reactive jOOQx provider
 *
 * @param <S> Type of {@link SqlClient}
 * @since 1.1.0
 */
public interface JooqxProvider<S extends SqlClient>
    extends BaseJooqxProvider<S, Tuple, JooqxPreparedQuery, RowSet<Row>, JooqxResultCollector, JooqxBase<S>> {

    @Override
    default @NotNull JooqxBase<S> createExecutor(Vertx vertx, DSLContext dsl, S sqlClient) {
        return JooqxBase.<S>baseBuilder()
                        .vertx(vertx)
                        .dsl(dsl)
                        .sqlClient(sqlClient)
                        .preparedQuery(createPreparedQuery())
                        .resultCollector(createResultCollector())
                        .errorConverter(errorConverter())
                        .typeMapperRegistry(typeMapperRegistry())
                        .build();
    }

}
