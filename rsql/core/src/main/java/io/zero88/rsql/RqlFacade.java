package io.zero88.rsql;

import io.zero88.rsql.criteria.CriteriaBuilder;
import io.zero88.rsql.criteria.CriteriaBuilderFactory;

import cz.jirutka.rsql.parser.ast.Node;
import lombok.NonNull;

/**
 * Represents for a facade that wraps some condiments to enhance parsing SQL query in runtime
 *
 * @since 1.0.0
 */
public interface RqlFacade {

    /**
     * Gets Query context.
     *
     * @return the query context
     * @see QueryContext
     */
    @NonNull QueryContext queryContext();

    /**
     * Criteria builder factory criteria builder factory.
     *
     * @param <T> Type of {@link Node}
     * @param <C> Type of {@link CriteriaBuilder}
     * @return the criteria builder factory
     * @see CriteriaBuilderFactory
     */
    @NonNull <T extends Node, C extends CriteriaBuilder<T>> CriteriaBuilderFactory<T, C> criteriaBuilderFactory();

}
