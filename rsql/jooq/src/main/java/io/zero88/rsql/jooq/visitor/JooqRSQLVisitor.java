package io.zero88.rsql.jooq.visitor;

import io.zero88.rsql.RSQLVisitor;
import io.zero88.rsql.jooq.JooqRSQLContext;

/**
 * The interface jOOQ RQL visitor.
 *
 * @param <R> Type of {@code Visitor Result}
 * @param <C> Type of {@link JooqRSQLContext}
 * @see RSQLVisitor
 * @since 1.0.0
 */
public interface JooqRSQLVisitor<R, C extends JooqRSQLContext> extends RSQLVisitor<R, C> {

}
