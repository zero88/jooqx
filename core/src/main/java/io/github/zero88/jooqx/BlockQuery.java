package io.github.zero88.jooqx;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.Block;
import org.jooq.Queries;
import org.jooq.Query;
import org.jooq.Statement;
import org.jooq.impl.DSL;

import io.github.zero88.jooqx.adapter.SQLResultAdapter;
import io.vertx.codegen.annotations.Fluent;

/**
 * A holder keeps multiple jOOQ queries and the relevant result adapter per each query
 *
 * @since 2.0.0
 */
public interface BlockQuery {

    /**
     * Create a wrapper for a collection of SQL queries.
     *
     * @return a block query
     * @see Queries
     * @see DSL#queries(Query...)
     */
    static BlockQuery create() {
        return new BlockQueryImpl(false);
    }

    /**
     * Create a procedural block
     *
     * @return a block query
     * @see Block
     * @see DSL#begin(Statement...)
     */
    static BlockQuery createBlock() {
        return new BlockQueryImpl(true);
    }

    boolean isInBlock();

    @NotNull List<Query> queries();

    @NotNull List<SQLResultAdapter> adapters();

    @Fluent
    @NotNull BlockQuery add(Query query, SQLResultAdapter adapter);

}
