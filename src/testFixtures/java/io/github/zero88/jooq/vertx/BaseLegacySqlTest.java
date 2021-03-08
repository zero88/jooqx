package io.github.zero88.jooq.vertx;

import org.testcontainers.containers.JdbcDatabaseContainer;

import io.github.zero88.jooq.vertx.AbstractSqlTest.AbstractDBContainerTest;
import io.github.zero88.jooq.vertx.AbstractSqlTest.AbstractDBMemoryTest;
import io.github.zero88.jooq.vertx.DBProvider.DBContainerProvider;
import io.github.zero88.jooq.vertx.DBProvider.DBMemoryProvider;
import io.github.zero88.jooq.vertx.LegacyJdbcSqlClientProvider.LegacyExecutorProvider;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;

public interface BaseLegacySqlTest<K, D extends DBProvider<K>>
    extends BaseSqlTest<SQLClient, JsonArray, ResultSet, VertxLegacyJdbcExecutor, K, D>, LegacyExecutorProvider,
            LegacyJdbcSqlClientProvider {

    @Override
    default SqlClientProvider<SQLClient> clientProvider() { return this; }

    @Override
    default LegacyExecutorProvider executorProvider() { return this; }

    abstract class AbstractLegacyDBCTest<K extends JdbcDatabaseContainer<?>>
        extends AbstractDBContainerTest<SQLClient, JsonArray, ResultSet, VertxLegacyJdbcExecutor, K>
        implements BaseLegacySqlTest<K, DBContainerProvider<K>> {

    }


    abstract class AbstractLegacyMemoryTest
        extends AbstractDBMemoryTest<SQLClient, JsonArray, ResultSet, VertxLegacyJdbcExecutor>
        implements BaseLegacySqlTest<String, DBMemoryProvider> {

    }

}
