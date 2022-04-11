package io.github.zero88.jooqx.adapter;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableLike;

import io.github.zero88.jooqx.datatype.DataTypeMapperRegistry;

abstract class SQLResultAdapterImpl<T extends TableLike<? extends Record>, O> implements SQLResultAdapter<T, O> {

    @NotNull
    private final T table;

    protected SQLResultAdapterImpl(@NotNull T table) {
        this.table = table;
    }

    protected final <R extends Record, I> RowConverterStrategy<R, I> initStrategy(@NotNull DSLContext dsl,
                                                                                  @NotNull DataTypeMapperRegistry registry,
                                                                                  @NotNull SQLCollectorPart<R, I> collectorPart) {
        return new RowConverterStrategyImpl<>(strategy(), table(), dsl, registry, collectorPart);
    }

    @Override
    public @NotNull T table() { return table; }

    abstract static class SelectResultInternal<T extends TableLike<? extends Record>, R extends Record, I, O>
        extends SQLResultAdapterImpl<T, O> {

        private final SQLCollectorPart<R, I> collectorPart;

        protected SelectResultInternal(@NotNull T table, @NotNull SQLCollectorPart<R, I> collectorPart) {
            super(table);
            this.collectorPart = collectorPart;
        }

        RowConverterStrategy<R, I> createStrategy(@NotNull DataTypeMapperRegistry registry, @NotNull DSLContext dsl) {
            return initStrategy(dsl, registry, collectorPart);
        }

    }

}
