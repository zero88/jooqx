package io.zero88.jooqx;

import io.zero88.jooqx.LegacySQLImpl.LegacySQLPQ;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.junit5.VertxTestContext;

public interface LegacySQLClientProvider extends SQLClientProvider<SQLClient> {

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

    default JsonObject sqlConfig(SQLConnectionOption opt) {
        return new JsonObject().put("provider_class", HikariCPDataSourceProvider.class.getName())
                               .put("jdbcUrl", opt.getJdbcUrl())
                               .put("username", opt.getUsername())
                               .put("password", opt.getPassword())
                               .put("driverClassName", opt.getDriverClassName());
    }

    interface LegacySQLExecutorProvider
        extends SQLExecutorProvider<SQLClient, JsonArray, ResultSet, LegacySQLExecutor> {

        @Override
        default LegacySQLExecutor createExecutor(Vertx vertx, JooqDSLProvider dslProvider, SQLClient sqlClient) {
            return LegacySQLExecutor.builder()
                                    .vertx(vertx)
                                    .dsl(dslProvider.dsl())
                                    .sqlClient(sqlClient)
                                    .preparedQuery(createPreparedQuery())
                                    .errorConverter(createErrorConverter())
                                    .build();
        }

        @Override
        default SQLPreparedQuery<JsonArray> createPreparedQuery() {
            return new LegacySQLPQ();
        }

    }

}
