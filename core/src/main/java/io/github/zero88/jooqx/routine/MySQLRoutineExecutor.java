package io.github.zero88.jooqx.routine;

import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.Routine;
import org.jooq.impl.AbstractRoutine;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JooqxBase;
import io.github.zero88.jooqx.SQLExecutor;
import io.github.zero88.jooqx.Utils;
import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.vertx.core.Future;
import io.vertx.sqlclient.SqlClient;

class MySQLRoutineExecutor extends RoutineExecutorDelegateImpl {

    @SuppressWarnings("rawtypes")
    MySQLRoutineExecutor(SQLExecutor jooqx) {
        super(jooqx);
    }

    /**
     * For MySQL function
     */
    @Override
    public <T> Future<T> routine(@NotNull Routine<T> routine) {
        final AbstractRoutine<T> r = validate(routine);
        if (hasReturnValue(routine)) {
            final Field<T> f = r.asField(r.getName());
            final Field<?>[] fields = getRoutineOutputFields(r, f);
            return jooqx.execute(dsl -> dsl.select(f), DSLAdapter.fetchOne(fields)).map(rec -> getReturnValue(r, rec));
        }
        return jooqx.sql(dsl -> dsl.renderInlined(r)).map(ignore -> null);
    }

    /**
     * For MySQL procedure
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Future<RoutineResult> routineResult(@NotNull Routine<T> routine) {
        if (Utils.isJDBC(jooqx.sqlClient())) {
            return new JDBCRoutineExecutor<>((JooqxBase<SqlClient>) jooqx).routineResult(routine);
        }
        final AbstractRoutine<T> r = validate(routine);
        final Field<?>[] fields = getRoutineOutputFields(r, r.asField(r.getName()));
        return jooqx.sqlQuery(dsl -> dsl.renderInlined(r), DSLAdapter.fetchOne(fields))
                    .map(rec -> new RoutineResultImpl(fields, rec));
    }

    @Override
    public <T, X, R> Future<R> routineResultSet(@NotNull Routine<T> routine,
                                                @NotNull SQLResultAdapter<X, R> resultAdapter) {
        return jooqx.sqlQuery(dsl -> dsl.renderInlined(routine), resultAdapter);
    }

}
