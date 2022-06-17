package io.github.zero88.jooqx.routine;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jooq.SQLDialect;

import io.github.zero88.jooqx.SQLExecutor;
import io.github.zero88.jooqx.SQLRoutineExecutor;

@Internal
public interface RoutineExecutorDelegate extends SQLRoutineExecutor {

    @SuppressWarnings("rawtypes")
    static RoutineExecutorDelegate init(@NotNull SQLExecutor jooqx) {
        final SQLDialect family = jooqx.dsl().family();
        final SQLDialect dialect = jooqx.dsl().dialect();
        if (SQLDialect.POSTGRES.supports(dialect)) {
            return new PostgresRoutineExecutor(jooqx);
        }
        if (SQLDialect.MYSQL.supports(dialect) || SQLDialect.MARIADB.supports(dialect)) {
            return new MySQLRoutineExecutor(jooqx);
        }
        if (family == SQLDialect.H2) {

        }
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
