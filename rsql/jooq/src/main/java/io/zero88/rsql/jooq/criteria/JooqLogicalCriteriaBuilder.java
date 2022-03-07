package io.zero88.rsql.jooq.criteria;

import java.util.function.BiFunction;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;
import org.jooq.impl.DSL;

import io.zero88.rsql.jooq.JooqRSQLContext;

import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.Node;

public interface JooqLogicalCriteriaBuilder<T extends LogicalNode> extends JooqCriteriaBuilder<T> {

    @Override
    default @NotNull Condition build(@NotNull JooqRSQLContext context) {
        final Condition[] condition = new Condition[] {DSL.noCondition()};
        boolean isFirst = true;
        for (Node node : node()) {
            if (isFirst) {
                condition[0] = condition[0].and(each(context, node));
            } else {
                condition[0] = logical().apply(condition[0], each(context, node));
            }
            isFirst = false;
        }
        return condition[0];
    }

    @NotNull
    default Condition each(@NotNull JooqRSQLContext context, @NotNull Node subNode) {
        return context.criteriaBuilderFactory().create(subNode).build(context);
    }

    @NotNull BiFunction<Condition, Condition, Condition> logical();

}
