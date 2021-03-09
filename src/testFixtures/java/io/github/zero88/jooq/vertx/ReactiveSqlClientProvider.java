package io.github.zero88.jooq.vertx;

import io.github.zero88.jooq.vertx.converter.ReactiveBindParamConverter;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

public interface ReactiveSqlClientProvider extends SqlClientProvider<SqlClient> {

    @Override
    default void closeClient(VertxTestContext context) {
        sqlClient().close(context.succeedingThenComplete());
    }

    interface ReactiveExecutorProvider
        extends JooqExecutorProvider<SqlClient, Tuple, RowSet<Row>, VertxReactiveSqlExecutor> {

        @Override
        default VertxReactiveSqlExecutor createExecutor(Vertx vertx, JooqDSLProvider dslProvider, SqlClient sqlClient) {
            return VertxReactiveSqlExecutor.builder()
                                           .vertx(vertx)
                                           .dsl(dslProvider.dsl())
                                           .sqlClient(sqlClient)
                                           .helper(createQueryHelper())
                                           .errorConverter(createErrorConverter())
                                           .build();
        }

        @Override
        default QueryHelper<Tuple> createQueryHelper() {
            return new QueryHelper<>(new ReactiveBindParamConverter());
        }

    }


    interface JdbcReactiveSqlClientProvider extends ReactiveSqlClientProvider {

        @Override
        default SqlClient createConnection(Vertx vertx, VertxTestContext ctx, SqlConnectionOption connOpt) {
            throw new UnsupportedOperationException("DataSource is in Pool as default");
        }

        @Override
        default SqlClient createPool(Vertx vertx, VertxTestContext ctx, SqlConnectionOption opt) {
            final JDBCPool pool = JDBCPool.pool(vertx, new JDBCConnectOptions().setJdbcUrl(opt.getJdbcUrl())
                                                                               .setDatabase(opt.getDatabase())
                                                                               .setUser(opt.getUsername())
                                                                               .setPassword(opt.getPassword()),
                                                poolOptions());
            ctx.completeNow();
            return pool;
        }

        @Override
        default boolean usePool() {
            return true;
        }

    }

}
