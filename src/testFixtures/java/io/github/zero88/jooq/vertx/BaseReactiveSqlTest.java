package io.github.zero88.jooq.vertx;

import org.testcontainers.containers.JdbcDatabaseContainer;

import io.github.zero88.jooq.vertx.AbstractSqlTest.AbstractDBContainerTest;
import io.github.zero88.jooq.vertx.AbstractSqlTest.AbstractDBMemoryTest;
import io.github.zero88.jooq.vertx.DBProvider.DBContainerProvider;
import io.github.zero88.jooq.vertx.DBProvider.DBMemoryProvider;
import io.github.zero88.jooq.vertx.ReactiveSqlClientProvider.ReactiveExecutorProvider;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

public interface BaseReactiveSqlTest<K, D extends DBProvider<K>>
    extends BaseSqlTest<SqlClient, Tuple, RowSet<Row>, VertxReactiveSqlExecutor, K, D>, ReactiveExecutorProvider,
            ReactiveSqlClientProvider {

    @Override
    default SqlClientProvider<SqlClient> clientProvider() { return this; }

    default ReactiveExecutorProvider executorProvider() { return this; }

    abstract class AbstractReactiveDBCTest<K extends JdbcDatabaseContainer<?>>
        extends AbstractDBContainerTest<SqlClient, Tuple, RowSet<Row>, VertxReactiveSqlExecutor, K>
        implements BaseReactiveSqlTest<K, DBContainerProvider<K>> {

    }


    abstract class AbstractReactiveMemoryTest
        extends AbstractDBMemoryTest<SqlClient, Tuple, RowSet<Row>, VertxReactiveSqlExecutor>
        implements BaseReactiveSqlTest<String, DBMemoryProvider> {

    }

}
