package io.zero88.rsql.criteria;

import org.jetbrains.annotations.NotNull;

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
     * Set up a builder
     *
     * @param node comparison node
     * @return a reference to this for fluent API
     */
    ComparisonCriteriaBuilder<T> setup(@NotNull ComparisonNode node);

    /**
     * Comparison operator proxy.
     *
     * @return the comparison operator
     * @since 1.0.0
     */
    @NotNull T operator();

}
