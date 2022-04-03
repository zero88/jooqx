package io.zero88.jooqx.adapter;

import java.util.function.Function;
import java.util.stream.Collector;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

final class RowConverterStrategyImpl<R extends Record, O> implements RowConverterStrategy<R, O> {

    private final SelectStrategy strategy;
    private final TableLike<? extends Record> table;
    private final DSLContext dsl;
    private final DataTypeMapperRegistry dataTypeRegistry;
    private final SQLCollectorPart<R, O> collectorPart;

    public RowConverterStrategyImpl(SelectStrategy strategy, TableLike<? extends Record> table, DSLContext dsl,
        DataTypeMapperRegistry dataTypeRegistry, SQLCollectorPart<R, O> collectorPart) {
        this.strategy         = strategy;
        this.table            = table;
        this.dsl              = dsl;
        this.dataTypeRegistry = dataTypeRegistry;
        this.collectorPart    = collectorPart;
    }

    @Override
    public @NotNull SelectStrategy strategy() {
        return strategy;
    }

    @Override
    public Field<?> lookupField(@NotNull String fieldName) {
        return table.field(fieldName);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Collector<Field<?>, R, O> createCollector(@NotNull Function<Field<?>, Object> getValue) {
        return Collector.of(() -> collectorPart.toRecord(dsl, table),
                            (r, f) -> r.set((Field<Object>) f, dataTypeRegistry.toUserType(f, getValue.apply(f))),
                            (r, r2) -> r2, collectorPart.converter());
    }

}
