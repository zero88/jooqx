package io.zero88.rsql.jooq;

import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jooq.TableLike;

import io.zero88.rsql.jooq.criteria.JooqCriteriaBuilderFactory;

@SuppressWarnings("rawtypes")
public final class JooqRSQLContextImpl implements JooqRSQLContext {

    private final TableLike table;
    private final JooqQueryContext queryContext;
    private final JooqCriteriaBuilderFactory criteriaBuilderFactory;

    JooqRSQLContextImpl(@NotNull TableLike table, JooqQueryContext queryContext,
        JooqCriteriaBuilderFactory criteriaBuilderFactory) {
        this.table                  = Objects.requireNonNull(table, "Required table");
        this.queryContext           = Optional.ofNullable(queryContext).orElse(JooqQueryContext.DEFAULT);
        this.criteriaBuilderFactory = Optional.ofNullable(criteriaBuilderFactory)
                                              .orElse(JooqCriteriaBuilderFactory.DEFAULT);
    }

    @Override
    public @NotNull TableLike subject() {
        return this.table;
    }

    @Override
    public @NotNull JooqQueryContext queryContext() {
        return queryContext;
    }

    @Override
    public @NotNull JooqCriteriaBuilderFactory criteriaBuilderFactory() {
        return criteriaBuilderFactory;
    }

}
