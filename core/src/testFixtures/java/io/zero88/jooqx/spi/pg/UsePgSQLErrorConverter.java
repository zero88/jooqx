package io.zero88.jooqx.spi.pg;

import io.vertx.sqlclient.SqlClient;
import io.zero88.jooqx.ReactiveTestDefinition.ReactiveJooqxProvider;
import io.zero88.jooqx.SQLErrorConverter;

public interface UsePgSQLErrorConverter<S extends SqlClient> extends ReactiveJooqxProvider<S> {

    @Override
    default SQLErrorConverter<? extends Throwable, ? extends RuntimeException> createErrorConverter() {
        return new PgErrorConverter();
    }

}
