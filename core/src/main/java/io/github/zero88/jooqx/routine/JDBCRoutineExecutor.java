package io.github.zero88.jooqx.routine;

import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Routine;
import org.jooq.impl.AbstractRoutine;

import io.github.zero88.jooqx.DSLAdapter;
import io.github.zero88.jooqx.JooqxBase;
import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.github.zero88.jooqx.adapter.SelectOne;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import io.vertx.jdbcclient.SqlOutParam;
import io.vertx.sqlclient.SqlClient;
import io.vertx.sqlclient.Tuple;

class JDBCRoutineExecutor<S extends SqlClient> extends RoutineExecutorDelegateImpl {

    JDBCRoutineExecutor(JooqxBase<S> jooqx) {
        super(jooqx);
    }

    @SuppressWarnings("unchecked")
    JooqxBase<S> jooqx() {
        return (JooqxBase<S>) jooqx;
    }

    @Override
    public <T> Future<RoutineResult> routineResult(@NotNull Routine<T> routine) {
        final AbstractRoutine<T> r = validate(routine);
        final Field<?>[] outFields = getRoutineOutputFields(r, r.asField(r.getName()));
        final SelectOne<Record> adapter = createOutParamAdapter(outFields);
        return routineResultSet(routine, adapter).map(record -> {
            final Record target = dsl().newRecord(outFields);
            target.fromArray(record.intoArray());
            return new RoutineResultImpl(outFields, target);
        });
    }

    @Override
    public <T, X, R> Future<@Nullable R> routineResultSet(@NotNull Routine<T> routine,
                                                          @NotNull SQLResultAdapter<X, R> adapter) {
        final Tuple bindValues = createBindValues(routine);
        return jooqx().sqlClient()
                      .preparedQuery(jooqx().preparedQuery().routine(dsl().configuration(), routine))
                      .execute(bindValues)
                      .map(rs -> jooqx().resultCollector().collect(rs, adapter, dsl(), jooqx().typeMapperRegistry()))
                      .otherwise(jooqx().errorConverter()::reThrowError);
    }

    @NotNull
    private <T> Tuple createBindValues(@NotNull Routine<T> routine) {
        final Tuple bindValues = jooqx().preparedQuery().routineValues(routine, jooqx().typeMapperRegistry());
        if (routine.getReturnParameter() != null) {
            bindValues.addValue(SqlOutParam.OUT(routine.getReturnParameter().getDataType().getSQLType()));
        }
        routine.getOutParameters()
               .forEach(parameter -> bindValues.addValue(SqlOutParam.OUT(parameter.getDataType().getSQLType())));
        return bindValues;
    }

    @NotNull
    private SelectOne<Record> createOutParamAdapter(Field<?>[] outFields) {
        return DSLAdapter.fetchOne(IntStream.range(0, outFields.length)
                                            .mapToObj(idx -> outFields[idx].as(String.valueOf(idx + 1)))
                                            .toArray(Field[]::new));
    }

}
