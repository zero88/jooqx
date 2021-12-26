package io.zero88.rsql.jooq.query;

import org.jooq.DSLContext;

import io.zero88.rsql.jooq.AbstractJooqRqlFacade;
import io.zero88.rsql.jooq.JooqRqlParser;
import io.zero88.rsql.jooq.JooqRqlQuery;

import lombok.Builder.Default;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@Accessors(fluent = true)
public abstract class AbstractJooqQuery<R, T, C> extends AbstractJooqRqlFacade implements JooqRqlQuery<R, T, C> {

    @NonNull
    private final DSLContext dsl;
    @NonNull
    @Default
    private final JooqRqlParser parser = JooqRqlParser.DEFAULT;

}
