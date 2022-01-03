package io.zero88.rsql.criteria;

import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

import cz.jirutka.rsql.parser.ast.ComparisonNode;
import lombok.NonNull;

/**
 * The interface Comparison criteria builder.
 *
 * @param <T> Type of {@code ComparisonOperatorProxy}
 * @see ComparisonNode
 * @see ComparisonOperatorProxy
 * @see CriteriaBuilder
 * @since 1.0.0
 */
public interface ComparisonCriteriaBuilder<T extends ComparisonOperatorProxy> extends CriteriaBuilder<ComparisonNode> {

    /**
     * Setup a builder
     *
     * @param node comparison node
     * @return the comparison criteria builder
     */
    ComparisonCriteriaBuilder<T> setup(@NonNull ComparisonNode node);

    /**
     * Comparison operator.
     *
     * @return the comparison operator
     * @since 1.0.0
     */
    @NonNull T operator();

}
