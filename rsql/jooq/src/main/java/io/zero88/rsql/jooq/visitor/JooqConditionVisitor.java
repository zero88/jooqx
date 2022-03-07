package io.zero88.rsql.jooq.visitor;

import org.jooq.Condition;

import io.zero88.rsql.jooq.JooqRSQLContext;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;

/**
 * The interface jOOQ condition RQL visitor.
 *
 * @see Condition
 * @since 1.0.0
 */
public final class JooqConditionVisitor implements JooqRSQLVisitor<Condition, JooqRSQLContext> {

    @Override
    public Condition visit(AndNode node, JooqRSQLContext context) {
        return context.criteriaBuilderFactory().create(node).build(context);
    }

    @Override
    public Condition visit(OrNode node, JooqRSQLContext context) {
        return context.criteriaBuilderFactory().create(node).build(context);
    }

    @Override
    public Condition visit(ComparisonNode node, JooqRSQLContext context) {
        return context.criteriaBuilderFactory().create(node).build(context);
    }

}
