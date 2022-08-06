package io.github.zero88.jooqx.routine;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jooq.SQLDialect;

import io.github.zero88.jooqx.SQLExecutor;
import io.github.zero88.jooqx.SQLRoutineExecutor;
import io.github.zero88.jooqx.Utils;

@Internal
public interface RoutineExecutorDelegate extends SQLRoutineExecutor {

    @SuppressWarnings("rawtypes")
    static RoutineExecutorDelegate init(@NotNull SQLExecutor jooqx) {
        if (Utils.isLegacyJDBC(jooqx.sqlClient())) {
            throw new UnsupportedOperationException(
                "Unsupported routine with legacy SQL client [" + jooqx.sqlClient().getClass() + "]");
        }
        final SQLDialect dialect = jooqx.dsl().dialect();
        if (SQLDialect.POSTGRES.supports(dialect)) {
            return new PostgresRoutineExecutor(jooqx);
        }
        if (SQLDialect.MYSQL.supports(dialect) || SQLDialect.MARIADB.supports(dialect)) {
            return new MySQLRoutineExecutor(jooqx);
        }
        if (SQLDialect.H2.supports(dialect)) {
            return new JDBCRoutineExecutor<>(
                (io.github.zero88.jooqx.JooqxBase<? extends io.vertx.sqlclient.SqlClient>) jooqx);
        }
        final SQLDialect family = jooqx.dsl().family();
        if ("oracle".equals(family.getNameLC())) {

        }
        if ("sqlserver".equals(family.getNameLC())) {

        }
        if ("db2".equals(family.getNameLC())) {

        }
        throw new UnsupportedOperationException(
            "Not yet supported routine for " + family + " with SQL client [" + jooqx.sqlClient().getClass() + "]");
    }

}
