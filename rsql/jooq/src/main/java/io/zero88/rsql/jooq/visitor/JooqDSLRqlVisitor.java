package io.zero88.rsql.jooq.visitor;

import org.jooq.DSLContext;

import io.zero88.rsql.jooq.JooqRqlVisitor;

/**
 * The interface jOOQ DSL RQL visitor.
 * <p>
 * It requires {@link DSLContext} to execute {@code jOOQ select}
 *
 * @param <R> Type of {@code Result}
 * @see DSLContext
 * @see JooqRqlVisitor
 * @since 1.0.0
 */
public interface JooqDSLRqlVisitor<R> extends JooqRqlVisitor<R, DSLContext> {

}
