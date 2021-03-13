package io.zero88.jooqx.adapter;

import java.util.function.Function;

import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.JsonRecord;
import io.zero88.jooqx.SQLResultConverter;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

abstract class SQLResultAdapterImpl<R, C extends SQLResultConverter<R>, T extends TableLike<? extends Record>, O>
    implements SQLResultAdapter<R, C, T, O> {

    @NonNull
    @Getter
    @Accessors(fluent = true)
    private final T table;
    @NonNull
    private final C converter;

    protected SQLResultAdapterImpl(@NonNull T table, @NonNull C converter) {
        this.table = table;
        this.converter = converter;
    }

    @Override
    @NonNull
    public final C converter() {
        return converter;
    }

    protected final RowConverterStrategyImpl createConverterStrategy(@NonNull SQLDataTypeRegistry registry) {
        return new RowConverterStrategyImpl(strategy(), registry, table());
    }

    abstract static class InternalSelectResultAdapter<RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>, I, O>
        extends SQLResultAdapterImpl<RS, C, T, O> {

        @Getter
        private final Function<JsonRecord<?>, I> mapper;

        protected InternalSelectResultAdapter(@NonNull T table, @NonNull C converter,
                                              @NonNull Function<JsonRecord<?>, I> mapper) {
            super(table, converter);
            this.mapper = mapper;
        }

    }

}
