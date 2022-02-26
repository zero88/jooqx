package io.zero88.rsql.jooq.query;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;

import io.zero88.rsql.jooq.AbstractJooqRqlFacade;
import io.zero88.rsql.jooq.JooqRqlParser;
import io.zero88.rsql.jooq.JooqRqlQuery;

import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class AbstractJooqQuery<R, T, C> extends AbstractJooqRqlFacade implements JooqRqlQuery<R, T, C> {

    @NotNull
    private final DSLContext dsl;
    @NotNull
    @Default
    private final JooqRqlParser parser = JooqRqlParser.DEFAULT;

    public @NotNull DSLContext dsl()       {return this.dsl;}

    public @NotNull JooqRqlParser parser() {return this.parser;}

}
