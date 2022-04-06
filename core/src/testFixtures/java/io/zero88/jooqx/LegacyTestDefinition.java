package io.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.JdbcDatabaseContainer;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.spi.DataSourceProvider;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.zero88.jooqx.SQLTest.LegacySQLTest;
import io.zero88.jooqx.SQLTestImpl.DBContainerSQLTest;
import io.zero88.jooqx.SQLTestImpl.DBMemorySQLTest;
import io.zero88.jooqx.provider.DBEmbeddedProvider.DBMemoryProvider;

public interface LegacyTestDefinition {

    //@formatter:off
    abstract class LegacyDBContainerTest<K extends JdbcDatabaseContainer<?>, P extends DataSourceProvider>
        extends DBContainerSQLTest<SQLClient, JsonArray, LegacySQLPreparedQuery, ResultSet, LegacySQLCollector, LegacyJooqx, K>
        implements LegacySQLTest<K, DBContainerProvider<K>, P> {

    }


    abstract class LegacyDBMemoryTest<P extends DataSourceProvider>
        extends DBMemorySQLTest<SQLClient, JsonArray, LegacySQLPreparedQuery, ResultSet, LegacySQLCollector, LegacyJooqx>
        implements LegacySQLTest<String, DBMemoryProvider, P> {

    }
    //@formatter:on


    interface LegacyRxHelper {

        default io.zero88.jooqx.reactivex.LegacyJooqx rxInstance(@NotNull LegacyJooqx jooqx) {
            final LegacyJooqx jooqx1 = LegacyJooqx.builder()
                                                  .setVertx(jooqx.vertx())
                                                  .setDSL(jooqx.dsl())
                                                  .setSqlClient(jooqx.sqlClient())
                                                  .setPreparedQuery(jooqx.preparedQuery())
                                                  .setResultCollector(jooqx.resultCollector())
                                                  .setErrorConverter(jooqx.errorConverter())
                                                  .setTypeMapperRegistry(jooqx.typeMapperRegistry())
                                                  .build();
            return io.zero88.jooqx.reactivex.LegacyJooqx.newInstance(jooqx1);
        }

    }

}
