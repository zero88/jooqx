package io.zero88.rsql.jooq.criteria.logical;

import java.util.function.BiFunction;

import org.jetbrains.annotations.NotNull;
import org.jooq.Condition;

import io.zero88.rsql.criteria.AbstractCriteriaBuilder;
import io.zero88.rsql.jooq.JooqRSQLContext;
import io.zero88.rsql.jooq.criteria.JooqLogicalCriteriaBuilder;

import cz.jirutka.rsql.parser.ast.OrNode;

public final class OrNodeCriteriaBuilder extends AbstractCriteriaBuilder<OrNode, JooqRSQLContext, Condition>
    implements JooqLogicalCriteriaBuilder<OrNode> {

    public OrNodeCriteriaBuilder(@NotNull OrNode node) {
        super(node);
    }

    @Override
    @NotNull
    public BiFunction<Condition, Condition, Condition> logical() {
        return Condition::or;
    }

}
