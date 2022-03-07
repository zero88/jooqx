package io.zero88.rsql.jooq.visitor;

import io.zero88.rsql.RSQLVisitor;
import io.zero88.rsql.jooq.JooqRSQLContext;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;

/**
 * The interface jOOQ RQL visitor.
 *
 * @param <R> Type of {@code Visitor Result}
 * @param <C> Type of {@code Visitor Context}
 * @see RSQLVisitor
 * @since 1.0.0
 */
public interface JooqRSQLVisitor<R, C extends JooqRSQLContext> extends RSQLVisitor<R, C> {

}
