package io.github.zero88.jooqx.adapter;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

@SuppressWarnings({ "unchecked", "rawtypes" })
public interface RecordFactoryHelper {

    @NotNull
    static BiFunction<String, Integer, Field<?>> fieldFinderByTable(TableLike<? extends Record> table) {
        return (fName, idx) -> Optional.ofNullable(table.field(fName)).orElseGet(() -> (Field) table.field(idx));
    }

    @NotNull
    static <REC extends Record, T extends TableLike<REC>> Function<DSLContext, REC> recordProviderByTable(T table) {
        return dsl -> table instanceof Table ? ((Table<REC>) table).newRecord() : dsl.newRecord(table.asTable());
    }

    @NotNull
    static BiFunction<String, Integer, Field<?>> fieldFinderByFields(Field<?>[] fields) {
        return (fName, idx) -> Arrays.stream(fields)
                                     .filter(f -> f.getName().equals(fName))
                                     .findAny()
                                     .orElseGet(() -> idx >= 0 && idx < fields.length ? fields[idx] : null);
    }

    @NotNull
    static <REC extends Record> BiFunction<String, Integer, Field<?>> fieldFinderByRecord(REC record) {
        return (fName, idx) -> Optional.ofNullable(record.field(fName)).orElseGet(() -> (Field) record.field(idx));
    }

}
