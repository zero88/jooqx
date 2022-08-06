package io.github.zero88.jooqx.provider;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

import io.github.zero88.jooqx.SQLExecutor;
import io.github.zero88.jooqx.SQLPreparedQuery;
import io.github.zero88.jooqx.SQLResultCollector;
import io.vertx.core.Vertx;

/**
 * Jooqx Provider
 *
 * @param <S>  Type of SQL client
 * @param <B>  Type of Vertx bind value holder depends on SQL client
 * @param <PQ> Type of SQL prepare query
 * @param <RC> Type of SQL result set collector
 * @param <E>  Type of SQL executor
 * @see SQLPreparedQuery
 * @see SQLResultCollector
 * @see SQLExecutor
 * @since 2.0.0
 */
public interface BaseJooqxProvider<S, B, PQ extends SQLPreparedQuery<B>, RC extends SQLResultCollector,
                                      E extends SQLExecutor<S, B, PQ, RC>>
    extends ErrorConverterProvider, TypeMapperRegistryProvider {

    @NotNull E createExecutor(Vertx vertx, DSLContext dsl, S sqlClient);

    default @Nullable PQ createPreparedQuery() {
        return null;
    }

    default @Nullable RC createResultCollector() {
        return null;
    }

}
