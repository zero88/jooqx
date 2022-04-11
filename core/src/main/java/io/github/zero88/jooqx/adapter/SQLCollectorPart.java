package io.github.zero88.jooqx.adapter;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.TableLike;

/**
 * SQL collector part that helps to create collector in {@link RowConverterStrategy#createCollector(Function)}
 * <p>
 * This collector part will be initialized in {@link SQLResultAdapter}
 *
 * @param <R> The type of jOOQ record
 * @param <I> The type of output object
 * @see RowConverterStrategy
 * @see SQLResultAdapter
 * @since 1.0.0
 */
public class SQLCollectorPart<R extends Record, I> {

    private final BiFunction<DSLContext, TableLike<? extends Record>, R> provider;
    private final Function<R, I> converter;

    public SQLCollectorPart(BiFunction<DSLContext, TableLike<? extends Record>, R> provider, Function<R, I> converter) {
        this.provider  = provider;
        this.converter = converter;
    }

    public Function<R, I> converter() { return converter; }

    public R toRecord(@NotNull DSLContext dsl, @NotNull TableLike<? extends Record> queryTable) {
        return provider.apply(dsl, queryTable);
    }

    protected BiFunction<DSLContext, TableLike<? extends Record>, R> provider() { return provider; }

    public static final class IdentityCollectorPart<R extends Record> extends SQLCollectorPart<R, R> {

        public IdentityCollectorPart(BiFunction<DSLContext, TableLike<? extends Record>, R> provider) {
            super(provider, Function.identity());
        }

        public <O> SQLCollectorPart<R, O> andThen(Function<R, O> converter) {
            return new SQLCollectorPart<>(provider(), converter);
        }

    }

}
