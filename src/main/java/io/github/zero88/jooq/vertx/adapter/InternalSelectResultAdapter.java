package io.github.zero88.jooq.vertx.adapter;

import java.util.List;
import java.util.function.BiFunction;

import org.jooq.Record;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.converter.ResultSetConverter;

import lombok.Getter;
import lombok.NonNull;

abstract class InternalSelectResultAdapter<RS, C extends ResultSetConverter<RS>,
                                              T extends TableLike<? extends Record>, I, O>
    extends SQLResultAdapterImpl<RS, C, T, O> {

    @Getter
    private final BiFunction<SQLResultAdapter<RS, C, T, O>, RS, List<I>> function;

    protected InternalSelectResultAdapter(@NonNull T table, @NonNull C converter,
                                          @NonNull BiFunction<SQLResultAdapter<RS, C, T, O>, RS, List<I>> function) {
        super(table, converter);
        this.function = function;
    }

}
