package io.zero88.jooqx;

import org.testcontainers.containers.JdbcDatabaseContainer;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.jdbc.spi.DataSourceProvider;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.junit5.VertxTestContext;
import io.zero88.jooqx.DBProvider.DBContainerProvider;
import io.zero88.jooqx.DBProvider.DBMemoryProvider;
import io.zero88.jooqx.LegacySQLImpl.LegacySQLPQ;
import io.zero88.jooqx.SQLTestImpl.DBContainerSQLTest;
import io.zero88.jooqx.SQLTestImpl.DBMemorySQLTest;

public interface LegacyTestDefinition {

    interface LegacyJooqxProvider
        extends JooqxProvider<SQLClient, JsonArray, ResultSet, LegacySQLCollector, LegacyJooqx> {

        @Override
        default LegacyJooqx createExecutor(Vertx vertx, JooqDSLProvider dslProvider, SQLClient sqlClient) {
            return LegacyJooqx.builder()
                              .vertx(vertx)
                              .dsl(dslProvider.dsl())
                              .sqlClient(sqlClient)
                              .preparedQuery(createPreparedQuery())
                              .errorConverter(errorConverter())
                              .typeMapperRegistry(typeMapperRegistry())
                              .build();
        }

        @Override
        default LegacySQLPreparedQuery createPreparedQuery() {
            return new LegacySQLPQ();
        }

    }


    interface LegacySQLClientProvider<P extends DataSourceProvider> extends SQLClientProvider<SQLClient> {

        default JsonObject sqlConfig(SQLConnectionOption opt) {
            return new JsonObject().put("provider_class", dataSourceProviderClass().getName())
                                   .put("jdbcUrl", opt.getJdbcUrl())
                                   .put("username", opt.getUsername())
                                   .put("password", opt.getPassword())
                                   .put("driverClassName", opt.getDriverClassName());
        }

        @Override
        default SQLClient createSqlClient(Vertx vertx, VertxTestContext ctx, SQLConnectionOption connOpt) {
            final JDBCClient client = JDBCClient.create(vertx, sqlConfig(connOpt));
            ctx.completeNow();
            return client;
        }

        @Override
        default void closeClient(VertxTestContext context) {
            sqlClient().close(context.succeedingThenComplete());
        }

        Class<P> dataSourceProviderClass();

    }


    interface LegacySQLTest<K, D extends DBProvider<K>, P extends DataSourceProvider>
        extends SQLTest<SQLClient, JsonArray, ResultSet, LegacySQLCollector, LegacyJooqx, K, D>,
                LegacyJooqxProvider, LegacySQLClientProvider<P> {

        @Override
        default SQLClientProvider<SQLClient> clientProvider() { return this; }

        @Override
        default LegacyJooqxProvider jooqxProvider() { return this; }

    }


    abstract class LegacyDBContainerTest<K extends JdbcDatabaseContainer<?>, P extends DataSourceProvider>
        extends DBContainerSQLTest<SQLClient, JsonArray, ResultSet, LegacySQLCollector, LegacyJooqx, K>
        implements LegacySQLTest<K, DBContainerProvider<K>, P> {

    }


    abstract class LegacyDBMemoryTest<P extends DataSourceProvider>
        extends DBMemorySQLTest<SQLClient, JsonArray, ResultSet, LegacySQLCollector, LegacyJooqx>
        implements LegacySQLTest<String, DBMemoryProvider, P> {

    }

}
