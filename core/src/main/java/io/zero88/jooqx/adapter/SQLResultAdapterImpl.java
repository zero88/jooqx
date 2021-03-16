package io.zero88.jooqx.adapter;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.datatype.DataTypeMapperRegistry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class SQLResultAdapterImpl<T extends TableLike<? extends Record>, O> implements SQLResultAdapter<T, O> {

    @NonNull
    private final T table;

    protected final <R extends Record, I> RowConverterStrategy<R, I> initStrategy(@NonNull DSLContext dsl,
                                                                                  @NonNull DataTypeMapperRegistry registry,
                                                                                  @NonNull SQLCollectorPart<R, I> collectorPart) {
        return new RowConverterStrategyImpl<>(strategy(), table(), dsl, registry, collectorPart);
    }

    abstract static class SelectResultInternal<T extends TableLike<? extends Record>, R extends Record, I, O>
        extends SQLResultAdapterImpl<T, O> {

        private final SQLCollectorPart<R, I> collectorPart;

        protected SelectResultInternal(@NonNull T table, @NonNull SQLCollectorPart<R, I> collectorPart) {
            super(table);
            this.collectorPart = collectorPart;
        }

        RowConverterStrategy<R, I> createStrategy(@NonNull DataTypeMapperRegistry registry, @NonNull DSLContext dsl) {
            return initStrategy(dsl, registry, collectorPart);
        }

    }

}
