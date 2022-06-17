package io.github.zero88.jooqx.routine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Field;
import org.jooq.Record;

public interface RoutineResult {

    @NotNull Field<?>[] getOutputFields();

    @Nullable Record getRecord();

}
