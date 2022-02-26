package io.zero88.rsql.jooq.criteria;

import org.jetbrains.annotations.NotNull;

import io.zero88.rsql.criteria.CriteriaBuilder;
import io.zero88.rsql.criteria.CriteriaBuilderFactory;
import io.zero88.rsql.jooq.criteria.logical.AndNodeCriteriaBuilder;
import io.zero88.rsql.jooq.criteria.logical.OrNodeCriteriaBuilder;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;

/**
 * The interface Criteria builder factory.
 *
 * @since 1.0.0
 */
public interface JooqCriteriaBuilderFactory extends CriteriaBuilderFactory<Node, JooqCriteriaBuilder<Node>> {

    /**
     * The default {@code jOOQ} criteria builder
     */
    JooqCriteriaBuilderFactory DEFAULT = new JooqCriteriaBuilderFactory() {};

    @Override
    default @NotNull CriteriaBuilder<AndNode> andNodeCriteriaBuilder(@NotNull AndNode node) {
        return new AndNodeCriteriaBuilder(node);
    }

    @Override
    default @NotNull CriteriaBuilder<OrNode> orNodeCriteriaBuilder(@NotNull OrNode node) {
        return new OrNodeCriteriaBuilder(node);
    }

    @Override
    default @NotNull CriteriaBuilder<ComparisonNode> comparisonNodeCriteriaBuilder(@NotNull ComparisonNode node) {
        return JooqComparisonCriteriaBuilderLoader.getInstance().get(node.getOperator()).setup(node);
    }

}
