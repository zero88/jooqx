package io.zero88.rsql.jooq.criteria;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;

import io.zero88.rsql.LikeWildcardPattern;
import io.zero88.rsql.criteria.AbstractCriteriaBuilder;
import io.zero88.rsql.criteria.ComparisonCriteriaBuilder;
import io.zero88.rsql.jooq.JooqArgumentParser;
import io.zero88.rsql.jooq.JooqQueryContext;
import io.zero88.rsql.jooq.JooqRSQLContext;
import io.zero88.rsql.parser.ast.ComparisonOperatorProxy;

import cz.jirutka.rsql.parser.ast.ComparisonNode;

public abstract class JooqComparisonCriteriaBuilder
    extends AbstractCriteriaBuilder<ComparisonNode, JooqRSQLContext, Condition>
    implements JooqCriteriaBuilder<ComparisonNode>,
               ComparisonCriteriaBuilder<ComparisonOperatorProxy, JooqRSQLContext, Condition> {

    @Override
    public final JooqComparisonCriteriaBuilder setup(@NotNull ComparisonNode node) {
        if (!operator().operator().equals(node.getOperator())) {
            throw new IllegalArgumentException(
                "Not match comparison operation [" + operator().operator() + "][" + node.getOperator() + "]");
        }
        this.node = node;
        return this;
    }

    @Override
    public @NotNull Condition build(@NotNull JooqRSQLContext context) {
        final JooqQueryContext queryCtx = context.queryContext();
        return queryCtx.fieldMapper()
                       .get(context.subject(), node().getSelector())
                       .map(f -> compare(f, node().getArguments(), queryCtx.argumentParser(), queryCtx.likeWildcard()))
                       .orElse(DSL.noCondition());
    }

    @SuppressWarnings("rawtypes")
    protected abstract @NotNull Condition compare(@NotNull Field field, @NotNull List<String> arguments,
        @NotNull JooqArgumentParser argParser, @NotNull LikeWildcardPattern wildcardPattern);

}
