package io.zero88.rsql.jooq;

import org.jetbrains.annotations.NotNull;

import io.zero88.rsql.RqlFacade;
import io.zero88.rsql.jooq.criteria.JooqCriteriaBuilderFactory;

/**
 * The interface jOOQ RQL facade that wraps some condiments to enhance parsing SQL query in runtime
 *
 * @since 1.0.0
 */
public interface JooqRqlFacade extends RqlFacade {

    /**
     * Gets Query context.
     *
     * @return the query context
     * @see JooqQueryContext
     * @since 1.0.0
     */
    default @NotNull JooqQueryContext queryContext() {
        return JooqQueryContext.DEFAULT;
    }

    /**
     * Criteria builder factory criteria builder factory.
     *
     * @return the criteria builder factory
     * @see JooqCriteriaBuilderFactory
     * @since 1.0.0
     */
    default @NotNull JooqCriteriaBuilderFactory criteriaBuilderFactory() {
        return JooqCriteriaBuilderFactory.DEFAULT;
    }

}
