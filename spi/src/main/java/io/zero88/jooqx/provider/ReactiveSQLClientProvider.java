package io.zero88.jooqx.provider;

import io.vertx.core.Future;
import io.vertx.sqlclient.SqlClient;

public interface ReactiveSQLClientProvider<S extends SqlClient> extends SQLClientProvider<S> {

    @Override
    default Future<Void> close() {
        return sqlClient().close();
    }

}
