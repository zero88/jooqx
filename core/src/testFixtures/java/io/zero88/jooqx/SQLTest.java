package io.zero88.jooqx;

public interface SQLTest<S, P, R, C extends SQLResultConverter<R>, E extends SQLExecutor<S, P, R, C>, K,
                            D extends DBProvider<K>>
    extends HasDSLProvider, HasDBProvider<K, D>, HasSQLClient<S> {

    SQLClientProvider<S> clientProvider();

    JooqxProvider<S, P, R, C, E> jooqxProvider();

}
