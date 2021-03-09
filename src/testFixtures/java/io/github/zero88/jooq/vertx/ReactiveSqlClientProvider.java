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

public interface ReactiveSqlClientProvider<S extends SqlClient> extends SqlClientProvider<S> {

    @Override
    default void closeClient(VertxTestContext context) {
        sqlClient().close(context.succeedingThenComplete());
    }

    interface ReactiveExecutorProvider<S extends SqlClient>
        extends JooqExecutorProvider<S, Tuple, RowSet<Row>, VertxReactiveSqlExecutor<S>> {

        @Override
        default VertxReactiveSqlExecutor<S> createExecutor(Vertx vertx, JooqDSLProvider dslProvider, S sqlClient) {
            return VertxReactiveSqlExecutor.<S>builder().vertx(vertx)
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


    interface JdbcReactiveSqlClientProvider extends ReactiveSqlClientProvider<JDBCPool> {

        @Override
        default JDBCPool createSqlClient(Vertx vertx, VertxTestContext ctx, SqlConnectionOption connOpt) {
            final JDBCPool pool = JDBCPool.pool(vertx, new JDBCConnectOptions().setJdbcUrl(connOpt.getJdbcUrl())
                                                                               .setDatabase(connOpt.getDatabase())
                                                                               .setUser(connOpt.getUsername())
                                                                               .setPassword(connOpt.getPassword()),
                                                poolOptions());
            ctx.completeNow();
            return pool;
        }

    }

}
