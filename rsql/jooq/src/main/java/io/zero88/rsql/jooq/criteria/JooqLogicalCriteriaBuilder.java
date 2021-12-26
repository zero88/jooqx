package io.zero88.rsql.jooq.criteria;

import java.util.function.BiFunction;

import org.jooq.Condition;
import org.jooq.TableLike;
import org.jooq.impl.DSL;

import io.zero88.rsql.jooq.JooqQueryContext;

import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.Node;
import lombok.NonNull;

public interface JooqLogicalCriteriaBuilder<T extends LogicalNode> extends JooqCriteriaBuilder<T> {

    @Override
    default @NonNull Condition build(@NonNull TableLike table, @NonNull JooqQueryContext queryContext,
                                     @NonNull JooqCriteriaBuilderFactory factory) {
        final Condition[] condition = new Condition[] {DSL.noCondition()};
        boolean isFirst = true;
        for (Node node : node()) {
            if (isFirst) {
                condition[0] = condition[0].and(each(table, queryContext, factory, node));
            } else {
                condition[0] = logical().apply(condition[0], each(table, queryContext, factory, node));
            }
            isFirst = false;
        }
        return condition[0];
    }

    @NonNull
    default Condition each(@NonNull TableLike table, @NonNull JooqQueryContext queryContext,
                           @NonNull JooqCriteriaBuilderFactory factory, @NonNull Node subNode) {
        return factory.create(subNode).build(table, queryContext, factory);
    }

    @NonNull BiFunction<Condition, Condition, Condition> logical();

}
