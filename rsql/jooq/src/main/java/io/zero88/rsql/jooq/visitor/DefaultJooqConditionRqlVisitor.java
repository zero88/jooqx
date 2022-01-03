package io.zero88.rsql.jooq.visitor;

import org.jooq.Condition;
import org.jooq.TableLike;

import io.zero88.rsql.jooq.AbstractJooqRqlFacade;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public final class DefaultJooqConditionRqlVisitor extends AbstractJooqRqlFacade implements JooqConditionRqlVisitor {

    @NonNull
    private final TableLike table;

    @Override
    public Condition visit(AndNode node) {
        return build(node);
    }

    @Override
    public Condition visit(OrNode node) {
        return build(node);
    }

    @Override
    public Condition visit(ComparisonNode node) {
        return build(node);
    }

    private Condition build(Node node) {
        return criteriaBuilderFactory().create(node).build(table, queryContext(), criteriaBuilderFactory());
    }

}
