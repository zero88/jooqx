package io.zero88.rsql;

import cz.jirutka.rsql.parser.ast.RSQLVisitor;

/**
 * The interface RQL visitor.
 *
 * @param <R> Type of {@code Visitor Result}
 * @param <C> Type of {@code Visitor Context}
 * @see RSQLVisitor
 * @since 1.0.0
 */
public interface RqlVisitor<R, C> extends RSQLVisitor<R, C>, RqlFacade, HasLog {}
