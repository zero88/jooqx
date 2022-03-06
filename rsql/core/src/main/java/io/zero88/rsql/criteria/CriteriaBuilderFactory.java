package io.zero88.rsql.criteria;

import org.jetbrains.annotations.NotNull;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;

/**
 * A factory that creates the criteria builder based on given AST node
 *
 * @param <T> Type of {@link Node}
 * @param <C> Type of {@link CriteriaBuilder}
 * @see Node
 */
public interface CriteriaBuilderFactory<T extends Node, C extends CriteriaBuilder<T>> {

    /**
     * Create criteria builder.
     *
     * @param node the node
     * @return the criteria builder
     * @since 1.0.0
     */
    @SuppressWarnings("unchecked")
    default C create(@NotNull T node) {
        if (node instanceof AndNode) {
            return (C) andNodeCriteriaBuilder((AndNode) node);
        }
        if (node instanceof OrNode) {
            return (C) orNodeCriteriaBuilder((OrNode) node);
        }
        if (node instanceof ComparisonNode) {
            return (C) comparisonNodeCriteriaBuilder((ComparisonNode) node);
        }
        throw new IllegalArgumentException("Unknown node type " + node.getClass().getSimpleName());
    }

    @NotNull CriteriaBuilder<AndNode> andNodeCriteriaBuilder(@NotNull AndNode node);

    @NotNull CriteriaBuilder<OrNode> orNodeCriteriaBuilder(@NotNull OrNode node);

    @NotNull CriteriaBuilder<ComparisonNode> comparisonNodeCriteriaBuilder(@NotNull ComparisonNode node);

}
