package io.zero88.rsql.jooq;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.TableLike;

import io.zero88.rsql.RSQLContext;
import io.zero88.rsql.jooq.criteria.JooqCriteriaBuilderFactory;

/**
 * The interface jOOQ RSQL context to enhance parsing SQL query in runtime
 *
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public interface JooqRSQLContext extends RSQLContext {

    /**
     * Defines the table to build SQL query
     *
     * @return the table
     * @see TableLike
     */
    @NotNull TableLike subject();

    /**
     * Gets Query context.
     *
     * @return the query context
     * @see JooqQueryContext
     */
    @NotNull JooqQueryContext queryContext();

    /**
     * Defines the Criteria builder factory
     *
     * @return the criteria builder factory
     * @see JooqCriteriaBuilderFactory
     */
    @NotNull JooqCriteriaBuilderFactory criteriaBuilderFactory();

    static JooqRSQLContext create(@NotNull TableLike table) {
        return create(table, null);
    }

    static JooqRSQLContext create(@NotNull TableLike table, @Nullable JooqQueryContext queryContext) {
        return create(table, queryContext, null);
    }

    static JooqRSQLContext create(@NotNull TableLike table, @Nullable JooqQueryContext queryContext,
        @Nullable JooqCriteriaBuilderFactory factory) {
        return new JooqRSQLContextImpl(table, queryContext, factory);
    }

}
