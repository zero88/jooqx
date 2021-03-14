package io.zero88.jooqx.adapter;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableLike;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@RequiredArgsConstructor
public class CollectorPart<R extends Record, O> {

    @Getter(value = AccessLevel.PROTECTED)
    private final BiFunction<DSLContext, TableLike<? extends Record>, R> provider;
    @Getter
    private final Function<R, O> converter;

    public R toRecord(@NonNull DSLContext dsl, @NonNull TableLike<? extends Record> queryTable) {
        return provider.apply(dsl, queryTable);
    }

    public static class IdentityCollectorPart<R extends Record> extends CollectorPart<R, R> {

        public IdentityCollectorPart(BiFunction<DSLContext, TableLike<? extends Record>, R> provider) {
            super(provider, Function.identity());
        }

        <O> CollectorPart<R, O> andThen(Function<R, O> converter) {
            return new CollectorPart<>(provider(), converter);
        }

    }

}
