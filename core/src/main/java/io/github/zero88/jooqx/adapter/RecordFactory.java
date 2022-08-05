package io.github.zero88.jooqx.adapter;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.TableLike;
import org.jooq.TableRecord;
import org.jooq.impl.DSL;

import io.github.zero88.jooqx.JsonRecord;

/**
 * Record factory defines the necessary methods to transform {@code Vert.x SQL row} to {@code jOOQ record} then map
 * record into a custom type.
 *
 * @param <REC> The type of jOOQ record
 * @param <R>   The type of expected result
 * @apiNote Record factory is only related to {@code jOOQ} functionality
 * @see RecordMapper
 * @since 2.0.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public interface RecordFactory<REC extends Record, R> extends RecordMapper<REC, R> {

    static <REC extends Record, T extends TableLike<REC>> RecordFactory<REC, REC> byTable(T table) {
        return RecordFactory.of(RecordFactoryHelper.fieldFinderByTable(table),
                                RecordFactoryHelper.recordProviderByTable(table));
    }

    static RecordFactory<JsonRecord<?>, JsonRecord<?>> byJson(TableLike<? extends Record> table) {
        return RecordFactory.of(RecordFactoryHelper.fieldFinderByTable(table),
                                dsl -> JsonRecord.create((TableLike<TableRecord>) table));
    }

    static <REC extends Record> RecordFactory<REC, REC> byRecord(REC record) {
        return RecordFactory.of(RecordFactoryHelper.fieldFinderByRecord(record),
                                dsl -> dsl.newRecord(DSL.table(record)));
    }

    static RecordFactory<Record, Record> byFields(Field<?>... fields) {
        return RecordFactory.of(RecordFactoryHelper.fieldFinderByFields(fields), dsl -> dsl.newRecord(fields));
    }

    static <REC extends Record, T extends TableLike<REC>, R> RecordFactory<REC, R> byClass(T tableLike,
                                                                                           Class<R> outputClass) {
        return byMapper(tableLike, record -> record.into(outputClass));
    }

    static <REC extends Record, T extends TableLike<REC>, R> RecordFactory<REC, R> byMapper(T table,
                                                                                            RecordMapper<REC, R> mapper) {
        return RecordFactory.of(RecordFactoryHelper.fieldFinderByTable(table),
                                RecordFactoryHelper.recordProviderByTable(table), mapper);
    }

    /**
     * Create new jOOQ record
     *
     * @param context DSL context
     * @return new record
     */
    @NotNull REC create(DSLContext context);

    /**
     * Lookup jOOQ field in current Query context
     *
     * @param fieldName  field name
     * @param fieldIndex field index
     * @return jOOQ field, it is nullable if not found field
     * @see FieldWrapper
     */
    @Nullable FieldWrapper lookup(String fieldName, int fieldIndex);

    static <REC extends Record> RecordFactory<REC, REC> of(BiFunction<String, Integer, Field<?>> fieldFinder,
                                                           Function<DSLContext, REC> recordProvider) {
        return RecordFactory.of(fieldFinder, recordProvider, rec -> rec);
    }

    static <REC extends Record, R> RecordFactory<REC, R> of(BiFunction<String, Integer, Field<?>> fieldFinder,
                                                            Function<DSLContext, REC> recordProvider,
                                                            RecordMapper<REC, R> recordMapper) {
        return new RecordFactory<REC, R>() {

            @Override
            public @Nullable R map(REC record) { return recordMapper.map(record); }

            @Override
            public @NotNull REC create(DSLContext context) { return recordProvider.apply(context); }

            @Override
            public @Nullable FieldWrapper lookup(String fieldName, int fieldIndex) {
                return FieldWrapper.create(fieldFinder.apply(fieldName, fieldIndex), fieldIndex);
            }
        };
    }

}
