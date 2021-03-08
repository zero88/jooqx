package io.github.zero88.jooq.vertx;

import io.github.zero88.jooq.vertx.converter.LegacyBindParamConverter;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.jdbc.spi.impl.HikariCPDataSourceProvider;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import io.vertx.junit5.VertxTestContext;

public interface LegacyJdbcSqlClientProvider extends SqlClientProvider<SQLClient> {

    @Override
    default boolean usePool() {
        return true;
    }

    @Override
    default SQLClient createConnection(Vertx vertx, VertxTestContext ctx, SqlConnectionOption connOpt) {
        throw new UnsupportedOperationException("DataSource is in Pool as default");
    }

    @Override
    default SQLClient createPool(Vertx vertx, VertxTestContext ctx, SqlConnectionOption opt) {
        final JDBCClient client = JDBCClient.create(vertx, sqlConfig(opt));
        ctx.completeNow();
        return client;
    }

    @Override
    default void closeClient(VertxTestContext context) {
        sqlClient().close(context.succeedingThenComplete());
    }

    default JsonObject sqlConfig(SqlConnectionOption opt) {
        return new JsonObject().put("provider_class", HikariCPDataSourceProvider.class.getName())
                               .put("jdbcUrl", opt.getJdbcUrl())
                               .put("username", opt.getUsername())
                               .put("password", opt.getPassword())
                               .put("driverClassName", opt.getDriverClassName());
    }

    interface LegacyExecutorProvider
        extends JooqExecutorProvider<SQLClient, JsonArray, ResultSet, VertxLegacyJdbcExecutor> {

        @Override
        default VertxLegacyJdbcExecutor createExecutor(Vertx vertx, JooqDSLProvider dslProvider, SQLClient sqlClient) {
            return VertxLegacyJdbcExecutor.builder()
                                          .vertx(vertx)
                                          .dsl(dslProvider.dsl())
                                          .sqlClient(sqlClient)
                                          .helper(createQueryHelper())
                                          .errorMaker(createErrorMaker())
                                          .build();
        }

        @Override
        default QueryHelper<JsonArray> createQueryHelper() {
            return new QueryHelper<>(new LegacyBindParamConverter());
        }

    }

}
