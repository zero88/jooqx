package io.github.zero88.jooqx.adapter;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;

import io.github.zero88.jooqx.adapter.RecordFactory.IdentityRecordFactory;

final class IdentityRecordFactoryImpl<REC extends Record> extends RecordFactoryImpl<REC, REC>
    implements IdentityRecordFactory<REC> {

    IdentityRecordFactoryImpl(BiFunction<String, Integer, Field<?>> finder, Function<DSLContext, REC> provider) {
        super(finder, provider, Function.identity());
    }

}
