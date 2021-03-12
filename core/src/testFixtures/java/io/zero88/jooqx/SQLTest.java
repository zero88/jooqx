package io.zero88.jooqx;

public interface SQLTest<S, P, R, E extends SQLExecutor<S, P, R>, K, D extends DBProvider<K>>
    extends HasDSLProvider, HasDBProvider<K, D>, HasSQLClient<S> {

    SQLClientProvider<S> clientProvider();

    JooqxProvider<S, P, R, E> jooqxProvider();

}