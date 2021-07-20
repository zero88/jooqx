package io.zero88.jooqx.provider;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.vertx.core.Vertx;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.ReactiveJooqxBase;
import io.zero88.jooqx.ReactiveSQLPreparedQuery;
import io.zero88.jooqx.ReactiveSQLResultCollector;

/**
 * Reactive jOOQx provider
 *
 * @param <S> Type of {@link SqlClient}
 * @since 1.1.0
 */
public interface ReactiveJooqxProvider<S extends SqlClient> extends
                                                            JooqxProvider<S, Tuple, ReactiveSQLPreparedQuery,
                                                                             RowSet<Row>, ReactiveSQLResultCollector,
                                                                             ReactiveJooqxBase<S>> {

    @Override
    default @NotNull ReactiveJooqxBase<S> createExecutor(Vertx vertx, DSLContext dsl, S sqlClient) {
        return ReactiveJooqxBase.<S>baseBuilder()
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
