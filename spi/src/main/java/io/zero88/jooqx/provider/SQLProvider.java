package io.zero88.jooqx.provider;

import org.jetbrains.annotations.NonNls;

import io.zero88.jooqx.SQLExecutor;
import io.zero88.jooqx.SQLPreparedQuery;
import io.zero88.jooqx.SQLResultCollector;

public interface SQLProvider<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>,
                                E extends SQLExecutor<S, B, PQ, RS, RC>>
    extends HasDSLProvider, HasSQLClient<S> {

    @NonNls SQLClientProvider<S> clientProvider();

    @NonNls JooqxProvider<S, B, PQ, RS, RC, E> jooqxProvider();

}
