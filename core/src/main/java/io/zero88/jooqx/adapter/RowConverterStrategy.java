package io.zero88.jooqx.adapter;

import java.util.Map;
import java.util.function.Supplier;

import org.jooq.Field;

import lombok.NonNull;

/**
 * @param <R> Type of output
 */
public interface RowConverterStrategy<R> extends HasStrategy {

    @NonNull Map<String, Field<?>> fieldMap();

    Supplier<R> newRecord();

    @NonNull R addFieldData(@NonNull R record, @NonNull Field<?> field, Object fieldValue);

}
