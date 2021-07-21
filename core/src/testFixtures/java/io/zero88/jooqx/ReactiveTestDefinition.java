package io.zero88.jooqx;

import org.testcontainers.containers.JdbcDatabaseContainer;

import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.SQLTest.ReactiveSQLTest;
import io.zero88.jooqx.SQLTestImpl.DBContainerSQLTest;
import io.zero88.jooqx.SQLTestImpl.DBMemorySQLTest;
import io.zero88.jooqx.provider.DBEmbeddedProvider.DBMemoryProvider;

import lombok.NonNull;

public interface ReactiveTestDefinition {

    //@formatter:off
    abstract class ReactiveDBContainerTest<S extends SqlClient, K extends JdbcDatabaseContainer<?>>
        extends DBContainerSQLTest<S, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>, ReactiveSQLResultCollector, ReactiveJooqxBase<S>, K>
        implements ReactiveSQLTest<S, K, DBContainerProvider<K>> {

    }

    abstract class ReactiveDBMemoryTest<S extends SqlClient>
        extends DBMemorySQLTest<S, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>, ReactiveSQLResultCollector, ReactiveJooqxBase<S>>
        implements ReactiveSQLTest<S, String, DBMemoryProvider> {

    }

    interface ReactiveRxHelper {

        default <S extends Pool> io.zero88.jooqx.reactivex.ReactiveJooqx rxPool(@NonNull ReactiveJooqxBase<S> jooqx) {
            return io.zero88.jooqx.reactivex.ReactiveJooqx.newInstance(ReactiveJooqx.builder()
                                         .vertx(jooqx.vertx())
                                         .dsl(jooqx.dsl())
                                         .sqlClient(jooqx.sqlClient())
                                         .preparedQuery(jooqx.preparedQuery())
                                         .resultCollector(jooqx.resultCollector())
                                         .errorConverter(jooqx.errorConverter())
                                         .typeMapperRegistry(jooqx.typeMapperRegistry())
                                         .build());
        }

        default <S extends SqlConnection> io.zero88.jooqx.reactivex.ReactiveJooqxConn rxConn(@NonNull ReactiveJooqxBase<S> jooqx) {
            return io.zero88.jooqx.reactivex.ReactiveJooqxConn.newInstance(ReactiveJooqxConn.builder()
                                                 .vertx(jooqx.vertx())
                                                 .dsl(jooqx.dsl())
                                                 .sqlClient(jooqx.sqlClient())
                                                 .preparedQuery(jooqx.preparedQuery())
                                                 .resultCollector(jooqx.resultCollector())
                                                 .errorConverter(jooqx.errorConverter())
                                                 .typeMapperRegistry(jooqx.typeMapperRegistry())
                                                 .build());
        }

    }
    //@formatter:on
}
