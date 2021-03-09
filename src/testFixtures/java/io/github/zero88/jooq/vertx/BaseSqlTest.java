package io.github.zero88.jooq.vertx;

public interface BaseSqlTest<S, P, R, E extends VertxJooqExecutor<S, P, R>, K, D extends DBProvider<K>>
    extends HasDSLProvider, HasDBProvider<K, D>, HasSqlClient<S> {

    SqlClientProvider<S> clientProvider();

    JooqExecutorProvider<S, P, R, E> executorProvider();

}
