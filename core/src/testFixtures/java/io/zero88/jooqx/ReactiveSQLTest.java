package io.zero88.jooqx;

import org.testcontainers.containers.JdbcDatabaseContainer;

import io.zero88.jooqx.BaseSQLTestImpl.DBContainerSQLTest;
import io.zero88.jooqx.BaseSQLTestImpl.DBMemorySQLTest;
import io.zero88.jooqx.DBProvider.DBContainerProvider;
import io.zero88.jooqx.DBProvider.DBMemoryProvider;
import io.zero88.jooqx.ReactiveSQLClientProvider.ReactiveSQLExecutorProvider;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

public interface ReactiveSQLTest<S extends SqlClient, K, D extends DBProvider<K>>
    extends BaseSQLTest<S, Tuple, RowSet<Row>, ReactiveSQLExecutor<S>, K, D>, ReactiveSQLExecutorProvider<S>,
            ReactiveSQLClientProvider<S> {

    @Override
    default SQLClientProvider<S> clientProvider() { return this; }

    default ReactiveSQLExecutorProvider<S> executorProvider() { return this; }

    abstract class ReactiveDBContainerTest<S extends SqlClient, K extends JdbcDatabaseContainer<?>>
        extends DBContainerSQLTest<S, Tuple, RowSet<Row>, ReactiveSQLExecutor<S>, K>
        implements ReactiveSQLTest<S, K, DBContainerProvider<K>> {

    }


    abstract class ReactiveDBMemoryTest<S extends SqlClient>
        extends DBMemorySQLTest<S, Tuple, RowSet<Row>, ReactiveSQLExecutor<S>>
        implements ReactiveSQLTest<S, String, DBMemoryProvider> {

    }

}
