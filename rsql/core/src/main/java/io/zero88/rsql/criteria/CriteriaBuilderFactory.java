package io.zero88.rsql.criteria;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import lombok.NonNull;

/**
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
    default C create(@NonNull T node) {
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

    @NonNull CriteriaBuilder<AndNode> andNodeCriteriaBuilder(@NonNull AndNode node);

    @NonNull CriteriaBuilder<OrNode> orNodeCriteriaBuilder(@NonNull OrNode node);

    @NonNull CriteriaBuilder<ComparisonNode> comparisonNodeCriteriaBuilder(@NonNull ComparisonNode node);

}
