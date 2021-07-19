package io.zero88.jooqx;

import io.zero88.jooqx.provider.SQLProvider;

public interface SQLTest<S, B, P extends SQLPreparedQuery<B>, R, C extends SQLResultCollector<R>,
                            E extends SQLExecutor<S, B, P, R, C>, K, D extends DBProvider<K>>
    extends SQLProvider<S, B, P, R, C, E>, HasDBProvider<K, D> {

}
