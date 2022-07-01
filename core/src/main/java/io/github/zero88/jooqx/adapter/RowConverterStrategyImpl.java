package io.github.zero88.jooqx.adapter;

import java.util.function.Function;
import java.util.stream.Collector;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.TableLike;

import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;

final class RowConverterStrategyImpl<REC extends Record, R> implements RowConverterStrategy<REC, R> {

    private final SelectStrategy strategy;
    private final TableLike<? extends Record> table;
    private final DSLContext dsl;
    private final DataTypeMapperRegistry dataTypeRegistry;
    private final SQLCollectorPart<REC, R> collectorPart;

    public RowConverterStrategyImpl(SelectStrategy strategy, TableLike<? extends Record> table, DSLContext dsl,
                                    DataTypeMapperRegistry dataTypeRegistry, SQLCollectorPart<REC, R> collectorPart) {
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
    public @NotNull Collector<Field<?>, REC, R> createCollector(@NotNull Function<Field<?>, Object> getValue) {
        return Collector.of(() -> collectorPart.toRecord(dsl, table),
                            (rec, f) -> rec.set((Field<Object>) f, dataTypeRegistry.toUserType(f, getValue.apply(f))),
                            (rec1, rec2) -> rec2, collectorPart.converter());
    }

}
