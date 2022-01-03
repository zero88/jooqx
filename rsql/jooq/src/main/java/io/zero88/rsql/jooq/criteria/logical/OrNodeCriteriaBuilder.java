package io.zero88.rsql.jooq.criteria.logical;

import java.util.function.BiFunction;

import org.jooq.Condition;

import io.zero88.rsql.criteria.AbstractCriteriaBuilder;
import io.zero88.rsql.jooq.criteria.JooqLogicalCriteriaBuilder;

import cz.jirutka.rsql.parser.ast.OrNode;
import lombok.NonNull;

public final class OrNodeCriteriaBuilder extends AbstractCriteriaBuilder<OrNode>
    implements JooqLogicalCriteriaBuilder<OrNode> {

    public OrNodeCriteriaBuilder(@NonNull OrNode node) {
        super(node);
    }

    @Override
    @NonNull
    public BiFunction<Condition, Condition, Condition> logical() {
        return Condition::or;
    }

}
