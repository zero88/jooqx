package io.github.zero88.jooqx.adapter;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;

class RecordFactoryImpl<REC extends Record, R> implements RecordFactory<REC, R> {

    private final BiFunction<String, Integer, Field<?>> finder;
    private final Function<DSLContext, REC> provider;
    private final Function<REC, R> converter;

    RecordFactoryImpl(BiFunction<String, Integer, Field<?>> finder, Function<DSLContext, REC> provider,
                      Function<REC, R> converter) {
        this.finder    = finder;
        this.provider  = provider;
        this.converter = converter;
    }

    @Override
    public @Nullable Field<?> lookup(String fieldName, int fieldIndex) {
        return this.finder.apply(fieldName, fieldIndex);
    }

    @Override
    public REC create(DSLContext context) {
        return this.provider.apply(context);
    }

    @Override
    public R convert(REC record) {
        return converter.apply(record);
    }

}
