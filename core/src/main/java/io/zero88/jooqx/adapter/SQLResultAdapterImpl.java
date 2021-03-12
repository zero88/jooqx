package io.zero88.jooqx.adapter;

import java.util.List;
import java.util.function.BiFunction;

import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.SQLResultConverter;

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

    @SuppressWarnings("unchecked")
    protected SQLResultAdapterImpl(@NonNull T table, @NonNull C converter) {
        this.table = table;
        this.converter = (C) converter.setup(strategy());
    }

    @Override
    @NonNull
    public final C converter() {
        return converter;
    }

    abstract static class InternalSelectResultAdapter<RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>, I, O>
        extends SQLResultAdapterImpl<RS, C, T, O> {

        @Getter
        private final BiFunction<SQLResultAdapter<RS, C, T, O>, RS, List<I>> function;

        protected InternalSelectResultAdapter(@NonNull T table, @NonNull C converter,
                                              @NonNull BiFunction<SQLResultAdapter<RS, C, T, O>, RS, List<I>> function) {
            super(table, converter);
            this.function = function;
        }

    }

}
