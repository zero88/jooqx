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

public interface BaseReactiveSqlTest<S extends SqlClient, K, D extends DBProvider<K>>
    extends BaseSqlTest<S, Tuple, RowSet<Row>, VertxReactiveSqlExecutor<S>, K, D>, ReactiveExecutorProvider<S>,
            ReactiveSqlClientProvider<S> {

    @Override
    default SqlClientProvider<S> clientProvider() { return this; }

    default ReactiveExecutorProvider<S> executorProvider() { return this; }

    abstract class AbstractReactiveDBCTest<S extends SqlClient, K extends JdbcDatabaseContainer<?>>
        extends AbstractDBContainerTest<S, Tuple, RowSet<Row>, VertxReactiveSqlExecutor<S>, K>
        implements BaseReactiveSqlTest<S, K, DBContainerProvider<K>> {

    }


    abstract class AbstractReactiveMemoryTest<S extends SqlClient>
        extends AbstractDBMemoryTest<S, Tuple, RowSet<Row>, VertxReactiveSqlExecutor<S>>
        implements BaseReactiveSqlTest<S, String, DBMemoryProvider> {

    }

}
