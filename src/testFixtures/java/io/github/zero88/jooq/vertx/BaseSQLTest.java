package io.github.zero88.jooq.vertx;

public interface BaseSQLTest<S, P, R, E extends SQLExecutor<S, P, R>, K, D extends DBProvider<K>>
    extends HasDSLProvider, HasDBProvider<K, D>, HasSQLClient<S> {

    SQLClientProvider<S> clientProvider();

    SQLExecutorProvider<S, P, R, E> executorProvider();

}
