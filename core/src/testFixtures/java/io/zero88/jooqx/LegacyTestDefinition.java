package io.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.JdbcDatabaseContainer;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.jdbc.spi.DataSourceProvider;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.zero88.jooqx.DBProvider.DBContainerProvider;
import io.zero88.jooqx.DBProvider.DBMemoryProvider;
import io.zero88.jooqx.SQLTestImpl.DBContainerSQLTest;
import io.zero88.jooqx.SQLTestImpl.DBMemorySQLTest;
import io.zero88.jooqx.provider.LegacyJooqxProvider;
import io.zero88.jooqx.provider.LegacySQLClientProvider;
import io.zero88.jooqx.provider.SQLClientProvider;

import lombok.NonNull;

public interface LegacyTestDefinition {

    interface LegacySQLTest<K, D extends DBProvider<K>, P extends DataSourceProvider>
        extends SQLTest<SQLClient, JsonArray, LegacySQLPreparedQuery, ResultSet, LegacySQLCollector, LegacyJooqx, K, D>,
                LegacyJooqxProvider, LegacySQLClientProvider<P> {

        @Override
        default @NotNull SQLClientProvider<SQLClient> clientProvider() { return this; }

        @Override
        default @NotNull LegacyJooqxProvider jooqxProvider() { return this; }

    }


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

        default io.zero88.jooqx.reactivex.LegacyJooqx rxInstance(@NonNull LegacyJooqx jooqx) {
            final LegacyJooqx jooqx1 = LegacyJooqx.builder()
                                                  .vertx(jooqx.vertx())
                                                  .dsl(jooqx.dsl())
                                                  .sqlClient(jooqx.sqlClient())
                                                  .preparedQuery(jooqx.preparedQuery())
                                                  .resultCollector(jooqx.resultCollector())
                                                  .errorConverter(jooqx.errorConverter())
                                                  .typeMapperRegistry(jooqx.typeMapperRegistry())
                                                  .build();
            return io.zero88.jooqx.reactivex.LegacyJooqx.newInstance(jooqx1);
        }

    }

}
