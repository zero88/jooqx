package io.zero88.jooqx.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

import io.vertx.core.Vertx;
import io.zero88.jooqx.SQLExecutor;
import io.zero88.jooqx.SQLPreparedQuery;
import io.zero88.jooqx.SQLResultCollector;

/**
 * Jooqx Provider
 *
 * @param <S>  Type of SQL client
 * @param <B>  Type of Vertx bind value holder depends on SQL client
 * @param <PQ> Type of SQL prepare query
 * @param <RS> Type of Vertx SQL result set holder
 * @param <RC> Type of SQL result set collector
 * @param <E>  Type of SQL executor
 * @see SQLPreparedQuery
 * @see SQLResultCollector
 * @see SQLExecutor
 * @since 1.1.0
 */
public interface BaseJooqxProvider<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>,
                                      E extends SQLExecutor<S, B, PQ, RS, RC>>
    extends ErrorConverterProvider, TypeMapperRegistryProvider {

    @NotNull E createExecutor(Vertx vertx, DSLContext dsl, S sqlClient);

    default @Nullable PQ createPreparedQuery() {
        return null;
    }

    default @Nullable RC createResultCollector() {
        return null;
    }

}
