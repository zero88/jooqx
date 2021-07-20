package io.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.JdbcDatabaseContainer;

import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.provider.DBProvider;
import io.zero88.jooqx.provider.DBEmbeddedProvider.DBMemoryProvider;
import io.zero88.jooqx.SQLTestImpl.DBContainerSQLTest;
import io.zero88.jooqx.SQLTestImpl.DBMemorySQLTest;
import io.zero88.jooqx.provider.ReactiveJooqxProvider;
import io.zero88.jooqx.provider.ReactiveSQLClientProvider;
import io.zero88.jooqx.provider.SQLClientProvider;

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


    interface ReactiveSQLTest<S extends SqlClient, K, D extends DBProvider<K>>
        extends SQLTest<S, Tuple, ReactiveSQLPreparedQuery, RowSet<Row>, ReactiveSQLResultCollector, ReactiveJooqxBase<S>, K, D>,
                ReactiveJooqxProvider<S>, ReactiveSQLClientProvider<S> {

        @Override
        default @NotNull SQLClientProvider<S> clientProvider() { return this; }

        default @NotNull ReactiveJooqxProvider<S> jooqxProvider() { return this; }

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
