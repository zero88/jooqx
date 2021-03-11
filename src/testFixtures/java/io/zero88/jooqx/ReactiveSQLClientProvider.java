package io.zero88.jooqx;

import io.zero88.jooqx.ReactiveSQLImpl.ReactiveSQLPQ;
import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.junit5.VertxTestContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

public interface ReactiveSQLClientProvider<S extends SqlClient> extends SQLClientProvider<S> {

    @Override
    default void closeClient(VertxTestContext context) {
        sqlClient().close(context.succeedingThenComplete());
    }

    interface ReactiveSQLExecutorProvider<S extends SqlClient>
        extends SQLExecutorProvider<S, Tuple, RowSet<Row>, ReactiveSQLExecutor<S>> {

        @Override
        default ReactiveSQLExecutor<S> createExecutor(Vertx vertx, JooqDSLProvider dslProvider, S sqlClient) {
            return ReactiveSQLExecutor.<S>builder().vertx(vertx)
                                                   .dsl(dslProvider.dsl())
                                                   .sqlClient(sqlClient)
                                                   .preparedQuery(createPreparedQuery())
                                                   .errorConverter(createErrorConverter())
                                                   .build();
        }

        @Override
        default SQLPreparedQuery<Tuple> createPreparedQuery() {
            return new ReactiveSQLPQ();
        }

    }


    interface ReactiveJDBCClientProvider extends ReactiveSQLClientProvider<JDBCPool> {

        @Override
        default JDBCPool createSqlClient(Vertx vertx, VertxTestContext ctx, SQLConnectionOption connOpt) {
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
