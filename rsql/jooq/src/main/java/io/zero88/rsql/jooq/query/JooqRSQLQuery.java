package io.zero88.rsql.jooq.query;

import org.jetbrains.annotations.NotNull;
import org.jooq.Query;

import io.zero88.rsql.HasLog;
import io.zero88.rsql.jooq.JooqRSQLParser;
import io.zero88.rsql.jooq.JooqRSQLQueryContext;

/**
 * The interface Jooq rql query.
 *
 * @param <R> Type of {@code Query Result}
 * @since 1.0.0
 */
public interface JooqRSQLQuery<R> extends HasLog {

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
     * Execute.
     *
     * @param query the query in RQL
     * @return the result
     * @since 1.0.0
     */
    @NotNull R execute(@NotNull String query);

    /**
     * To jOOQ query
     *
     * @param query the query in RQL
     * @return jOOQ query
     */
    @NotNull Query toQuery(@NotNull String query);

}
