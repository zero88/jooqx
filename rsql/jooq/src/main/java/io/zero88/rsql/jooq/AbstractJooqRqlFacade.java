package io.zero88.rsql.jooq;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import io.zero88.rsql.jooq.criteria.JooqCriteriaBuilderFactory;

import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class AbstractJooqRqlFacade implements JooqRqlFacade {

    private final JooqQueryContext queryContext;
    private final JooqCriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    public @NotNull JooqQueryContext queryContext() {
        return Optional.ofNullable(queryContext).orElseGet(JooqRqlFacade.super::queryContext);
    }

    @Override
    public @NotNull JooqCriteriaBuilderFactory criteriaBuilderFactory() {
        return Optional.ofNullable(criteriaBuilderFactory).orElseGet(JooqRqlFacade.super::criteriaBuilderFactory);
    }

}
