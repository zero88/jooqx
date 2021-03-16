package io.zero88.jooqx;

public interface SQLTest<S, B, P extends SQLPreparedQuery<B>, R, C extends SQLResultCollector<R>,
                            E extends SQLExecutor<S, B, P, R, C>, K, D extends DBProvider<K>>
    extends HasDSLProvider, HasDBProvider<K, D>, HasSQLClient<S> {

    SQLClientProvider<S> clientProvider();

    JooqxProvider<S, B, P, R, C, E> jooqxProvider();

}
