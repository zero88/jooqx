package io.zero88.jooqx.provider;

import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.zero88.jooqx.SQLExecutor;
import io.zero88.jooqx.SQLPreparedQuery;
import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.provider.DBEmbeddedProvider.DBFileProvider;

/**
 * Jooqx facade for database in local file
 *
 * @see JooqxFacade
 * @see DBFileProvider
 * @since 1.1.0
 */
public abstract class DBFileJooqxFacade<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>,
                                           E extends SQLExecutor<S, B, PQ, RS, RC>>
    implements JooqxFacade<S, B, PQ, RS, RC, E>, DBFileProvider {

    @Override
    public Future<E> jooqx(Vertx vertx, DSLContext dsl, JsonObject connOptions, @Nullable JsonObject poolOptions) {
        return JooqxFacade.super.jooqx(vertx, dsl, optimizeConnOptions(init(), connOptions), poolOptions);
    }

    protected JsonObject optimizeConnOptions(String databaseName, JsonObject connOptions) {
        return DBFileProvider.super.createConnOptions(databaseName)
                                   .mergeIn(connOptions)
                                   .put("username", user(connOptions))
                                   .put("password", password(connOptions));
    }

}
