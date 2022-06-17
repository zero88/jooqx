package io.github.zero88.jooqx.routine;

import java.util.Optional;

import org.jooq.Field;
import org.jooq.Record;

class RoutineResultImpl implements RoutineResult {

    private final Field<?>[] outFields;
    private final Record record;

    RoutineResultImpl(Field<?>[] outFields, Record record) {
        this.outFields = Optional.ofNullable(outFields).orElseGet(() -> new Field[0]);
        this.record    = record;
    }

    @Override
    public Field<?>[] getOutputFields() {
        return outFields;
    }

    @Override
    public Record getRecord() {
        return record;
    }

    @Override
    public String toString() {
        return record != null ? record.toString() : "";
    }

}
