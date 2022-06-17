package io.github.zero88.jooqx.routine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Routine;
import org.jooq.impl.AbstractRoutine;
import org.jooq.impl.DSL;

import io.github.zero88.jooqx.SQLExecutor;
import io.vertx.core.Future;

@SuppressWarnings("rawtypes")
abstract class RoutineExecutorDelegateImpl implements RoutineExecutorDelegate {

    protected final SQLExecutor jooqx;

    protected RoutineExecutorDelegateImpl(SQLExecutor jooqx) { this.jooqx = jooqx; }

    @Override
    public final @NotNull DSLContext dsl() {
        return jooqx.dsl();
    }

    @Override
    public <T> Future<T> routine(@NotNull Routine<T> routine) {
        return routineResult(routine).map(r -> getReturnValue(routine, r.getRecord()));
    }

    protected final <T> AbstractRoutine<T> validate(@NotNull Routine<T> routine) {
        if (routine instanceof AbstractRoutine) {
            return (AbstractRoutine<T>) routine;
        }
        throw new UnsupportedOperationException("Unsupported routine: " + routine.getClass());
    }

    protected final <T> Field<?>[] getRoutineOutputFields(Routine<T> routine, Field<T> routineAsField) {
        if (!routine.getOutParameters().isEmpty()) {
            return routine.getOutParameters()
                          .stream()
                          .map(p -> DSL.field(DSL.name(p.getName()), p.getDataType()))
                          .toArray(Field[]::new);
        }
        return new Field[] { routineAsField };
    }

    protected final <T> boolean isReturnsVoidOrResultSet(Routine<T> routine) {
        return routine.getReturnParameter() == null && routine.getOutParameters().isEmpty();
    }

    protected final <T> boolean hasReturnValue(Routine<T> routine) {
        return routine.getReturnParameter() != null;
    }

    protected final <T> Class<T> getReturnClass(Routine<T> routine) {
        return routine.getReturnParameter() == null ? null : routine.getReturnParameter().getType();
    }

    protected final <T> T getReturnValue(Routine<T> routine, @Nullable Record record) {
        final Class<T> returnCls = getReturnClass(routine);
        if (returnCls == null || record == null) {
            return null;
        }
        return record.get(0, returnCls);
    }

}
