package io.zero88.rsql.jooq.query;

import java.util.Objects;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.Query;

import io.zero88.rsql.jooq.JooqRSQLParser;
import io.zero88.rsql.jooq.JooqRSQLQueryContext;

public abstract class AbstractJooqQuery<Q extends Query, R> implements JooqRSQLQuery<Q, R> {

    private final JooqRSQLParser parser;
    private final JooqRSQLQueryContext context;

    protected AbstractJooqQuery(@Nullable JooqRSQLParser parser, @NotNull JooqRSQLQueryContext context) {
        this.parser  = Optional.ofNullable(parser).orElse(JooqRSQLParser.DEFAULT);
        this.context = Objects.requireNonNull(context, "Required jOOQ RSQL query context");
    }

    public @NotNull JooqRSQLParser parser() {
        return this.parser;
    }

    public @NotNull JooqRSQLQueryContext context() {
        return this.context;
    }

}
