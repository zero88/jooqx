package io.zero88.jooqx.adapter;

import java.util.function.Function;

import org.jooq.Record;
import org.jooq.TableLike;

import io.zero88.jooqx.JsonRecord;
import io.zero88.jooqx.SQLResultCollector;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
abstract class SelectResultInternal<RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>, I, O>
    extends SelectAdhocResultAdapter<RS, C, T, O> {

    @Getter
    private final Function<JsonRecord<?>, I> mapper;

    protected SelectResultInternal(@NonNull T table, @NonNull C converter, @NonNull Function<JsonRecord<?>, I> mapper) {
        super(table, converter);
        this.mapper = mapper;
    }

}
