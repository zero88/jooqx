package io.github.zero88.jooq.vertx;

import org.testcontainers.containers.JdbcDatabaseContainer;

import io.github.zero88.jooq.vertx.BaseSQLTestImpl.DBContainerSQLTest;
import io.github.zero88.jooq.vertx.BaseSQLTestImpl.DBMemorySQLTest;
import io.github.zero88.jooq.vertx.DBProvider.DBContainerProvider;
import io.github.zero88.jooq.vertx.DBProvider.DBMemoryProvider;
import io.github.zero88.jooq.vertx.ReactiveSQLClientProvider.ReactiveSQLExecutorProvider;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

public interface ReactiveSQLTest<S extends SqlClient, K, D extends DBProvider<K>>
    extends BaseSQLTest<S, Tuple, RowSet<Row>, VertxReactiveSQLExecutor<S>, K, D>, ReactiveSQLExecutorProvider<S>,
            ReactiveSQLClientProvider<S> {

    @Override
    default SQLClientProvider<S> clientProvider() { return this; }

    default ReactiveSQLExecutorProvider<S> executorProvider() { return this; }

    abstract class ReactiveDBContainerTest<S extends SqlClient, K extends JdbcDatabaseContainer<?>>
        extends DBContainerSQLTest<S, Tuple, RowSet<Row>, VertxReactiveSQLExecutor<S>, K>
        implements ReactiveSQLTest<S, K, DBContainerProvider<K>> {

    }


    abstract class ReactiveDBMemoryTest<S extends SqlClient>
        extends DBMemorySQLTest<S, Tuple, RowSet<Row>, VertxReactiveSQLExecutor<S>>
        implements ReactiveSQLTest<S, String, DBMemoryProvider> {

    }

}
