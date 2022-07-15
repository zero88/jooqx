package io.github.zero88.jooqx.adapter;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.TableRecord;
import org.jooq.impl.DSL;

import io.github.zero88.jooqx.JsonRecord;

/**
 * Record factory
 *
 * @param <REC> The type of jOOQ record
 * @param <R>   The type of expected result
 * @since 2.0.0
 */
public interface RecordFactory<REC extends Record, R> {

    interface IdentityRecordFactory<REC extends Record> extends RecordFactory<REC, REC> { }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    static IdentityRecordFactory<JsonRecord<?>> byJson(TableLike<? extends Record> tableLike) {
        return new IdentityRecordFactoryImpl<>((fName, idx) -> tableLike.field(fName),
                                               dsl -> JsonRecord.create((TableLike<TableRecord>) tableLike));
    }

    static <REC extends Record> IdentityRecordFactory<REC> byRecord(REC record) {
        return new IdentityRecordFactoryImpl<>((fName, idx) -> record.field(fName),
                                               dsl -> dsl.newRecord(DSL.table(record)));
    }

    static IdentityRecordFactory<Record> byFields(Field<?>... fields) {
        return new IdentityRecordFactoryImpl<>(
            (fName, idx) -> Arrays.stream(fields).filter(f -> f.getName().equals(fName)).findAny().orElse(null),
            dsl -> dsl.newRecord(fields));
    }

    static IdentityRecordFactory<Record> byFields(Collection<Field<?>> fields) {
        return new IdentityRecordFactoryImpl<>(
            (fName, idx) -> fields.stream().filter(f -> f.getName().equals(fName)).findAny().orElse(null),
            dsl -> dsl.newRecord(fields));
    }

    static <R> RecordFactory<? extends Record, R> byClass(TableLike<? extends Record> tableLike, Class<R> outputClass) {
        return byConverter(tableLike, record -> record.into(outputClass));
    }

    @SuppressWarnings("unchecked")
    static <REC extends Record, T extends TableLike<REC>> IdentityRecordFactory<REC> byTable(T table) {
        return new IdentityRecordFactoryImpl<>((fName, idx) -> table.field(fName), dsl -> table instanceof Table
                                                                                          ?
                                                                                          ((Table<REC>) table).newRecord()
                                                                                          : dsl.newRecord(
                                                                                              table.asTable()));
    }

    @SuppressWarnings("unchecked")
    static <REC extends Record, R> RecordFactory<REC, R> byConverter(TableLike<? extends Record> tableLike,
                                                                     Function<REC, R> converter) {
        return new RecordFactoryImpl<>((fieldName, idx) -> tableLike.field(fieldName),
                                       dsl -> (REC) dsl.newRecord(tableLike.fields()), converter);
    }

    /**
     * Create new jOOQ record
     *
     * @param context DSL context
     * @return new record
     */
    REC create(DSLContext context);

    /**
     * Lookup jOOQ field in current jOOQ Query context
     *
     * @param fieldName  field name
     * @param fieldIndex field index
     * @return jOOQ field, it is nullable
     */
    @Nullable Field<?> lookup(String fieldName, int fieldIndex);

    /**
     * Convert jOOQ record to an expected output
     *
     * @param record the record
     * @return an expected result
     */
    R convert(REC record);

}
