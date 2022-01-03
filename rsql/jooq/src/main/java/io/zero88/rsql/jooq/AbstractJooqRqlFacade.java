package io.zero88.rsql.jooq;

import java.util.Optional;

import io.zero88.rsql.jooq.criteria.JooqCriteriaBuilderFactory;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
public abstract class AbstractJooqRqlFacade implements JooqRqlFacade {

    private final JooqQueryContext queryContext;
    private final JooqCriteriaBuilderFactory criteriaBuilderFactory;

    @Override
    public @NonNull JooqQueryContext queryContext() {
        return Optional.ofNullable(queryContext).orElseGet(JooqRqlFacade.super::queryContext);
    }

    @Override
    public @NonNull JooqCriteriaBuilderFactory criteriaBuilderFactory() {
        return Optional.ofNullable(criteriaBuilderFactory).orElseGet(JooqRqlFacade.super::criteriaBuilderFactory);
    }

}
