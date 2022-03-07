package io.zero88.rsql.jooq;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.TableLike;

/**
 * Represents for jOOQ RSQL query context
 *
 * @see JooqRSQLContext
 * @since 1.0.0
 */
public interface JooqRSQLQueryContext extends JooqRSQLContext {

    /**
     * Defines dsl context.
     *
     * @return the dsl context
     * @see DSLContext
     */
    @NotNull DSLContext dsl();

    @SuppressWarnings("rawtypes")
    static JooqRSQLQueryContext create(@NotNull DSLContext dslContext, @NotNull TableLike table) {
        return new JooqRSQLQueryContextImpl(dslContext, JooqRSQLContext.create(table));
    }

    static JooqRSQLQueryContext create(@NotNull DSLContext dslContext, @NotNull JooqRSQLContext context) {
        return new JooqRSQLQueryContextImpl(dslContext, context);
    }

}
