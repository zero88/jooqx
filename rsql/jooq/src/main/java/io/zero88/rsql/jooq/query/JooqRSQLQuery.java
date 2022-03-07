package io.zero88.rsql.jooq.query;

import org.jetbrains.annotations.NotNull;
import org.jooq.Query;

import io.zero88.rsql.HasLog;
import io.zero88.rsql.jooq.JooqRSQLParser;
import io.zero88.rsql.jooq.JooqRSQLQueryContext;

/**
 * The interface jOOQ RSQL query.
 *
 * @param <Q> Type of {@link Query}
 * @param <R> Type of {@code Query Result}
 * @since 1.0.0
 */
public interface JooqRSQLQuery<Q extends Query, R> extends HasLog {

    /**
     * Defines Jooq RQL Parser.
     *
     * @return the Jooq rql parser
     * @see JooqRSQLParser
     * @since 1.0.0
     */
    @NotNull JooqRSQLParser parser();

    /**
     * Defines RSQL query context.
     *
     * @return the RSQL query context
     * @see JooqRSQLQueryContext
     * @since 1.0.0
     */
    @NotNull JooqRSQLQueryContext context();

    /**
     * Converts to jOOQ query.
     *
     * @param query the RQL query
     * @return the jOOQ query
     * @see Query
     */
    @NotNull Q toQuery(@NotNull String query);

    /**
     * Execute query.
     *
     * @param query the RQL query
     * @return the query result
     * @since 1.0.0
     */
    @NotNull R execute(@NotNull String query);

}
