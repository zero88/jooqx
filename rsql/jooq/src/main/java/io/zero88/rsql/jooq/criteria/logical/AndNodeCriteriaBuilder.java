package io.zero88.rsql.jooq.criteria.logical;

import java.util.function.BiFunction;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;

import io.zero88.rsql.criteria.AbstractCriteriaBuilder;
import io.zero88.rsql.jooq.criteria.JooqLogicalCriteriaBuilder;

import cz.jirutka.rsql.parser.ast.AndNode;

public final class AndNodeCriteriaBuilder extends AbstractCriteriaBuilder<AndNode>
    implements JooqLogicalCriteriaBuilder<AndNode> {

    public AndNodeCriteriaBuilder(@NotNull AndNode node) {
        super(node);
    }

    @Override
    @NotNull
    public BiFunction<Condition, Condition, Condition> logical() {
        return Condition::and;
    }

}
