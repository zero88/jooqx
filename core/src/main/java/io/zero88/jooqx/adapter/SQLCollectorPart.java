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

/**
 * SQL collector part that helps creating collector in {@link RowConverterStrategy#createCollector(Function)}
 * <p>
 * This collector part will be init in SQLResultAdapter
 *
 * @param <R> The jOOQ record type
 * @param <I> The output object type
 * @see RowConverterStrategy
 * @see SQLResultAdapter
 */
@Accessors(fluent = true)
@RequiredArgsConstructor
public class SQLCollectorPart<R extends Record, I> {

    @Getter(value = AccessLevel.PROTECTED)
    private final BiFunction<DSLContext, TableLike<? extends Record>, R> provider;
    @Getter
    private final Function<R, I> converter;

    public R toRecord(@NonNull DSLContext dsl, @NonNull TableLike<? extends Record> queryTable) {
        return provider.apply(dsl, queryTable);
    }

    public static final class IdentityCollectorPart<R extends Record> extends SQLCollectorPart<R, R> {

        public IdentityCollectorPart(BiFunction<DSLContext, TableLike<? extends Record>, R> provider) {
            super(provider, Function.identity());
        }

        public <O> SQLCollectorPart<R, O> andThen(Function<R, O> converter) {
            return new SQLCollectorPart<>(provider(), converter);
        }

    }

}
