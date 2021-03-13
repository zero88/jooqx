package io.zero88.jooqx;

import org.testcontainers.containers.JdbcDatabaseContainer;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.DBProvider.DBContainerProvider;
import io.zero88.jooqx.DBProvider.DBMemoryProvider;
import io.zero88.jooqx.ReactiveSQLImpl.ReactiveSQLPQ;
import io.zero88.jooqx.SQLTestImpl.DBContainerSQLTest;
import io.zero88.jooqx.SQLTestImpl.DBMemorySQLTest;

public interface ReactiveTestDefinition {

    interface ReactiveJooqxProvider<S extends SqlClient>
        extends JooqxProvider<S, Tuple, RowSet<Row>, ReactiveSQLResultCollector, ReactiveJooqx<S>> {

        @Override
        default ReactiveJooqx<S> createExecutor(Vertx vertx, JooqDSLProvider dslProvider, S sqlClient) {
            return ReactiveJooqx.<S>builder().vertx(vertx)
                                             .dsl(dslProvider.dsl())
                                             .sqlClient(sqlClient)
                                             .preparedQuery(createPreparedQuery())
                                             .errorConverter(errorConverter())
                                             .build();
        }

        @Override
        default ReactiveSQLPreparedQuery createPreparedQuery() {
            return new ReactiveSQLPQ();
        }

    }


    abstract class ReactiveDBContainerTest<S extends SqlClient, K extends JdbcDatabaseContainer<?>>
        extends DBContainerSQLTest<S, Tuple, RowSet<Row>, ReactiveSQLResultCollector, ReactiveJooqx<S>, K>
        implements ReactiveSQLTest<S, K, DBContainerProvider<K>> {

    }


    abstract class ReactiveDBMemoryTest<S extends SqlClient>
        extends DBMemorySQLTest<S, Tuple, RowSet<Row>, ReactiveSQLResultCollector, ReactiveJooqx<S>>
        implements ReactiveSQLTest<S, String, DBMemoryProvider> {

    }


    interface ReactiveSQLClientProvider<S extends SqlClient> extends SQLClientProvider<S> {

        @Override
        default void closeClient(VertxTestContext context) {
            sqlClient().close(context.succeedingThenComplete());
        }

    }


    interface ReactiveSQLTest<S extends SqlClient, K, D extends DBProvider<K>>
        extends SQLTest<S, Tuple, RowSet<Row>, ReactiveSQLResultCollector, ReactiveJooqx<S>, K, D>,
                ReactiveJooqxProvider<S>, ReactiveSQLClientProvider<S> {

        @Override
        default SQLClientProvider<S> clientProvider() { return this; }

        default ReactiveJooqxProvider<S> jooqxProvider() { return this; }

    }

}
