package io.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.JdbcDatabaseContainer;

import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import io.zero88.jooqx.SQLTest.JooqxTest;
import io.zero88.jooqx.SQLTestImpl.DBContainerSQLTest;
import io.zero88.jooqx.SQLTestImpl.DBMemorySQLTest;
import io.zero88.jooqx.provider.DBEmbeddedProvider.DBMemoryProvider;

public interface JooqxTestDefinition {

    //@formatter:off
    abstract class JooqxDBContainerTest<S extends SqlClient, K extends JdbcDatabaseContainer<?>>
        extends DBContainerSQLTest<S, Tuple, JooqxPreparedQuery, RowSet<Row>, JooqxResultCollector, JooqxBase<S>, K>
        implements JooqxTest<S, K, DBContainerProvider<K>> {

    }

    abstract class JooqxDBMemoryTest<S extends SqlClient>
        extends DBMemorySQLTest<S, Tuple, JooqxPreparedQuery, RowSet<Row>, JooqxResultCollector, JooqxBase<S>>
        implements JooqxTest<S, String, DBMemoryProvider> {

    }

    interface JooqxRxHelper {

        default <S extends Pool> io.zero88.jooqx.reactivex.Jooqx rxPool(@NotNull JooqxBase<S> jooqx) {
            return io.zero88.jooqx.reactivex.Jooqx.newInstance(Jooqx.builder()
                                                                 .setVertx(jooqx.vertx())
                                                                 .setDSL(jooqx.dsl())
                                                                 .setSqlClient(jooqx.sqlClient())
                                                                 .setPreparedQuery(jooqx.preparedQuery())
                                                                 .setResultCollector(jooqx.resultCollector())
                                                                 .setErrorConverter(jooqx.errorConverter())
                                                                 .setTypeMapperRegistry(jooqx.typeMapperRegistry())
                                                                 .build());
        }

        default <S extends SqlConnection> io.zero88.jooqx.reactivex.JooqxConn rxConn(@NotNull JooqxBase<S> jooqx) {
            return io.zero88.jooqx.reactivex.JooqxConn.newInstance(JooqxConn.builder()
                                                                         .setVertx(jooqx.vertx())
                                                                         .setDSL(jooqx.dsl())
                                                                         .setSqlClient(jooqx.sqlClient())
                                                                         .setPreparedQuery(jooqx.preparedQuery())
                                                                         .setResultCollector(jooqx.resultCollector())
                                                                         .setErrorConverter(jooqx.errorConverter())
                                                                         .setTypeMapperRegistry(jooqx.typeMapperRegistry())
                                                                         .build());
        }

    }
    //@formatter:on
}
