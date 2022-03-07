package io.zero88.rsql.jooq.query;

import org.jetbrains.annotations.NotNull;

import io.zero88.rsql.jooq.JooqRSQLParser;
import io.zero88.rsql.jooq.JooqRSQLQueryContext;

import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class AbstractJooqQuery<R> implements JooqRSQLQuery<R> {

    @Default
    private final JooqRSQLParser parser = JooqRSQLParser.DEFAULT;
    private final JooqRSQLQueryContext context;

    public @NotNull JooqRSQLQueryContext context() {return this.context;}

    public @NotNull JooqRSQLParser parser()        {return this.parser;}

}
