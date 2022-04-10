package io.zero88.jooqx.provider;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.Jooqx;
import io.zero88.jooqx.JooqxBase;
import io.zero88.jooqx.JooqxConn;
import io.zero88.jooqx.JooqxPreparedQuery;
import io.zero88.jooqx.JooqxResultCollector;

/**
 * Represents for Jooqx Reactive facade
 *
 * @param <S> Type of reactive SQL client
 * @see JooqxBase
 * @see Jooqx
 * @see JooqxConn
 * @see SqlClient
 * @since 2.0.0
 */
public interface JooqxFacade<S extends SqlClient>
    extends BaseJooqxFacade<S, Tuple, JooqxPreparedQuery, RowSet<Row>, JooqxResultCollector, JooqxBase<S>> {

}
