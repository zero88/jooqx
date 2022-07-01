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
 * @param <REC> The type of jOOQ record
 * @param <R>   The type of output object
 * @see RowConverterStrategy
 * @see SQLResultAdapter
 * @since 1.0.0
 */
public class SQLCollectorPart<REC extends Record, R> {

    private final BiFunction<DSLContext, TableLike<? extends Record>, REC> provider;
    private final Function<REC, R> converter;

    public SQLCollectorPart(BiFunction<DSLContext, TableLike<? extends Record>, REC> provider,
                            Function<REC, R> converter) {
        this.provider  = provider;
        this.converter = converter;
    }

    public Function<REC, R> converter() { return converter; }

    public REC toRecord(@NotNull DSLContext dsl, @NotNull TableLike<? extends Record> queryTable) {
        return provider.apply(dsl, queryTable);
    }

    protected BiFunction<DSLContext, TableLike<? extends Record>, REC> provider() { return provider; }

    public static final class IdentityCollectorPart<REC extends Record> extends SQLCollectorPart<REC, REC> {

        public IdentityCollectorPart(BiFunction<DSLContext, TableLike<? extends Record>, REC> provider) {
            super(provider, Function.identity());
        }

        public <R> SQLCollectorPart<REC, R> andThen(Function<REC, R> converter) {
            return new SQLCollectorPart<>(provider(), converter);
        }

    }

}
