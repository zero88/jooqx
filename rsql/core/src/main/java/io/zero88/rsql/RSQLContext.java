package io.zero88.rsql;

import org.jetbrains.annotations.NotNull;

import io.zero88.rsql.criteria.CriteriaBuilder;
import io.zero88.rsql.criteria.CriteriaBuilderFactory;

import cz.jirutka.rsql.parser.ast.Node;

/**
 * Represents for a RSQL runtime context to enhance parsing SQL query in runtime
 *
 * @since 1.0.0
 */
public interface RSQLContext {

    /**
     * Defines the subject to build SQL query
     *
     * @param <S> Type of subject
     * @return the subject
     */
    @NotNull <S> S subject();

    /**
     * Gets Query context.
     *
     * @return the query context
     * @see QueryContext
     */
    @NotNull QueryContext queryContext();

    /**
     * Gets the Criteria builder factory
     *
     * @param <T> Type of {@link Node}
     * @param <C> Type of {@link CriteriaBuilder}
     * @return the criteria builder factory
     * @see CriteriaBuilderFactory
     */
    @NotNull <T extends Node, C extends CriteriaBuilder<T>> CriteriaBuilderFactory<T, C> criteriaBuilderFactory();

}
