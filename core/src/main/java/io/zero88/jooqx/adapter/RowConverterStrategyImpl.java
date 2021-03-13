package io.zero88.jooqx.adapter;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.TableLike;
import org.jooq.TableRecord;

import io.zero88.jooqx.JsonRecord;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
final class RowConverterStrategyImpl implements RowConverterStrategy<JsonRecord<?>> {

    private final SelectStrategy strategy;
    private final SQLDataTypeRegistry dataTypeRegistry;
    private final Map<String, Field<?>> fieldMap;
    private final Supplier<JsonRecord<? extends TableRecord>> supplier;

    public RowConverterStrategyImpl(@NonNull SelectStrategy strategy, @NonNull SQLDataTypeRegistry dataTypeRegistry,
                                    @NonNull TableLike<? extends Record> table) {
        this.strategy = strategy;
        this.dataTypeRegistry = dataTypeRegistry;
        this.fieldMap = table.fieldStream().collect(Collectors.toMap(Field::getName, Function.identity()));
        this.supplier = () -> JsonRecord.create((TableLike<TableRecord>) table);
    }

    @Override
    public Supplier<JsonRecord<?>> newRecord() {
        return supplier;
    }

    @Override
    public @NonNull JsonRecord<?> addFieldData(@NonNull JsonRecord<?> record, @NonNull Field<?> field,
                                               Object fieldValue) {
        record.set((Field<Object>) field, dataTypeRegistry.convertFieldType(field, fieldValue));
        return record;
    }

}
