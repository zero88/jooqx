package io.zero88.jooqx;

import io.zero88.jooqx.provider.DBProvider;
import io.zero88.jooqx.provider.JooqxFacade;

/**
 * SQL test interface
 *
 * @see JooqxFacade
 * @see HasDBProvider
 */
public interface SQLTest<S, B, PQ extends SQLPreparedQuery<B>, RS, RC extends SQLResultCollector<RS>,
                            E extends SQLExecutor<S, B, PQ, RS, RC>, DB, DBP extends DBProvider<DB>>
    extends JooqxFacade<S, B, PQ, RS, RC, E>, HasDBProvider<DB, DBP> {

}
