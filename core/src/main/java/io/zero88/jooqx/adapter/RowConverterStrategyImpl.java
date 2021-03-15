package io.zero88.jooqx.adapter;

import java.util.function.Function;
import java.util.stream.Collector;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor
final class RowConverterStrategyImpl<R extends Record, O> implements RowConverterStrategy<R, O> {

    @Getter
    private final SelectStrategy strategy;
    private final TableLike<? extends Record> table;
    private final DSLContext dsl;
    private final DataTypeMapperRegistry dataTypeRegistry;
    private final CollectorPart<R, O> collectorPart;

    @Override
    public Field<?> lookupField(@NonNull String fieldName) {
        return table.field(fieldName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collector<Field<?>, R, O> createCollector(@NonNull Function<Field<?>, Object> getValue) {
        return Collector.of(() -> collectorPart.toRecord(dsl, table),
                            (r, f) -> r.set((Field<Object>) f, dataTypeRegistry.toUserType(f, getValue.apply(f))),
                            (r, r2) -> r2, collectorPart.converter());
    }

}
