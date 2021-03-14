package io.zero88.jooqx.adapter;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
abstract class SQLResultAdapterImpl<RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>, O>
    implements SQLResultAdapter<RS, C, T, O> {

    @NonNull
    private final T table;
    @NonNull
    private final C converter;

    protected final <R extends Record, I> RowConverterStrategy<R, I> initStrategy(@NonNull DSLContext dsl,
                                                                                  @NonNull SQLDataTypeRegistry registry,
                                                                                  @NonNull CollectorPart<R, I> collectorPart) {
        return new RowConverterStrategyImpl<>(strategy(), table(), dsl, registry, collectorPart);
    }

    abstract static class SelectResultInternal<RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>, R extends Record, I, O>
        extends SQLResultAdapterImpl<RS, C, T, O> {

        private final CollectorPart<R, I> collectorPart;

        protected SelectResultInternal(@NonNull T table, @NonNull C converter,
                                       @NonNull CollectorPart<R, I> collectorPart) {
            super(table, converter);
            this.collectorPart = collectorPart;
        }

        RowConverterStrategy<R, I> createStrategy(@NonNull SQLDataTypeRegistry registry, @NonNull DSLContext dsl) {
            return initStrategy(dsl, registry, collectorPart);
        }

    }

}
