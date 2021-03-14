package io.zero88.jooqx.adapter;

import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.Record;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class CollectorPart<R extends Record, O> {

    @Getter(value = AccessLevel.PROTECTED)
    private final Function<DSLContext, R> provider;
    @Getter
    private final Function<R, O> converter;

    public R toRecord(DSLContext dsl) {
        return provider.apply(dsl);
    }

    public static class IdentityCollectorPart<R extends Record> extends CollectorPart<R, R> {

        public IdentityCollectorPart(Function<DSLContext, R> provider) {
            super(provider, Function.identity());
        }

        <O> CollectorPart<R, O> andThen(Function<R, O> converter) {
            return new CollectorPart<>(provider(), converter);
        }

    }

}
