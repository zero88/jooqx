package io.zero88.rsql.jooq.query;

import org.jetbrains.annotations.NotNull;
import org.jooq.Query;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class AbstractJooqConditionQuery<R> extends AbstractJooqQuery<R> implements JooqConditionQuery<R> {

    public @NotNull R execute(@NotNull String query) {
        return execute(parser().criteria(query, context()));
    }

    @Override
    public @NotNull Query toQuery(@NotNull String query) {
        return toQuery(parser().criteria(query, context()));
    }

}
