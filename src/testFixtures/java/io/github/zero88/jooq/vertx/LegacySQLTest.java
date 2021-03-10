package io.github.zero88.jooq.vertx;

import org.testcontainers.containers.JdbcDatabaseContainer;

import io.github.zero88.jooq.vertx.BaseSQLTestImpl.DBContainerSQLTest;
import io.github.zero88.jooq.vertx.BaseSQLTestImpl.DBMemorySQLTest;
import io.github.zero88.jooq.vertx.DBProvider.DBContainerProvider;
import io.github.zero88.jooq.vertx.DBProvider.DBMemoryProvider;
import io.github.zero88.jooq.vertx.LegacySQLClientProvider.LegacySQLExecutorProvider;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;

public interface LegacySQLTest<K, D extends DBProvider<K>>
    extends BaseSQLTest<SQLClient, JsonArray, ResultSet, VertxLegacySQLExecutor, K, D>, LegacySQLExecutorProvider,
            LegacySQLClientProvider {

    @Override
    default SQLClientProvider<SQLClient> clientProvider() { return this; }

    @Override
    default LegacySQLExecutorProvider executorProvider() { return this; }

    abstract class LegacyDBContainerTest<K extends JdbcDatabaseContainer<?>>
        extends DBContainerSQLTest<SQLClient, JsonArray, ResultSet, VertxLegacySQLExecutor, K>
        implements LegacySQLTest<K, DBContainerProvider<K>> {

    }


    abstract class LegacyDBMemoryTest extends DBMemorySQLTest<SQLClient, JsonArray, ResultSet, VertxLegacySQLExecutor>
        implements LegacySQLTest<String, DBMemoryProvider> {

    }

}
