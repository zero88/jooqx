package io.github.zero88.jooqx.routine;

import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.Routine;
import org.jooq.impl.AbstractRoutine;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.SQLExecutor;
import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.vertx.core.Future;

class PostgresRoutineExecutor extends RoutineExecutorDelegateImpl {

    @SuppressWarnings("rawtypes")
    PostgresRoutineExecutor(SQLExecutor jooqx) {
        super(jooqx);
    }

    @Override
    public <T> Future<RoutineResult> routineResult(@NotNull Routine<T> routine) {
        final AbstractRoutine<T> r = validate(routine);
        final Field<T> f = r.asField(r.getName());
        final Field<?>[] out = getRoutineOutputFields(r, f);
        return jooqx.execute(dsl -> dsl.select(out).from("{0}", r.asField()), DSLAdapter.fetchOne(out))
                    .map(rec -> new RoutineResultImpl(out, rec));
    }

    @Override
    public <T, X, R> Future<R> routineResultSet(@NotNull Routine<T> routine,
                                                @NotNull SQLResultAdapter<X, R> resultAdapter) {
        return Future.failedFuture(new UnsupportedOperationException(
            "PostgreSQL uses a routine that returns a result set, which is similar to doing a query from the table."));
    }

}
