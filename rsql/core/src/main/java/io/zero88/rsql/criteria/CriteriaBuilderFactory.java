package io.zero88.rsql.criteria;

import org.jetbrains.annotations.NotNull;

import io.zero88.rsql.RSQLContext;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;

/**
 * A factory that creates the criteria builder based on given AST node
 *
 * @param <N> Type of {@link Node}
 * @param <B> Type of {@link CriteriaBuilder}
 * @param <C> Type of {@link RSQLContext}
 * @param <P> Type of SQL predicate
 * @since 1.0.0
 */
public interface CriteriaBuilderFactory<N extends Node, C extends RSQLContext, P, B extends CriteriaBuilder<N, C, P>> {

    /**
     * Create criteria builder.
     *
     * @param node the node
     * @return the criteria builder
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    default B create(@NotNull N node) {
        if (node instanceof AndNode) {
            return (B) andNodeCriteriaBuilder((AndNode) node);
        }
        if (node instanceof OrNode) {
            return (B) orNodeCriteriaBuilder((OrNode) node);
        }
        if (node instanceof ComparisonNode) {
            return (B) comparisonNodeCriteriaBuilder((ComparisonNode) node);
        }
        throw new IllegalArgumentException("Unknown node type " + node.getClass().getSimpleName());
    }

    @NotNull CriteriaBuilder<AndNode, C, P> andNodeCriteriaBuilder(@NotNull AndNode node);

    @NotNull CriteriaBuilder<OrNode, C, P> orNodeCriteriaBuilder(@NotNull OrNode node);

    @NotNull CriteriaBuilder<ComparisonNode, C, P> comparisonNodeCriteriaBuilder(@NotNull ComparisonNode node);

}
