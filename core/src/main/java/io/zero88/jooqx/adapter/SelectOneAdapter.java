package io.zero88.jooqx.adapter;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.zero88.jooqx.JsonRecord;
import io.zero88.jooqx.SQLResultConverter;
import io.zero88.jooqx.adapter.HasStrategy.SelectOne;
import io.zero88.jooqx.adapter.SQLResultAdapterImpl.InternalSelectResultAdapter;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.NonNull;

/**
 * Select one result adapter that defines output in {@code single element}
 *
 * @param <R> Type of Vertx Result set
 * @param <C> Type of result set converter
 * @param <T> Type of jOOQ Table
 * @param <O> Type of an expectation output
 * @since 1.0.0
 */
public final class SelectOneAdapter<R, C extends SQLResultConverter<R>, T extends TableLike<? extends Record>, O>
    extends InternalSelectResultAdapter<R, C, T, O, O> implements SelectOne {

    protected SelectOneAdapter(@NonNull T table, @NonNull C converter, @NonNull Function<JsonRecord<?>, O> mapper) {
        super(table, converter, mapper);
    }

    @Override
    public O collect(@NonNull R resultSet, @NonNull SQLDataTypeRegistry registry) {
        return converter().collect(resultSet, createConverterStrategy(registry))
                          .stream()
                          .findFirst()
                          .map(getMapper())
                          .orElse(null);
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>> SelectOneAdapter<RS, C, T, JsonRecord<?>> jsonRecord(
        @NonNull T table, @NonNull C converter) {
        return new SelectOneAdapter<>(table, converter, Function.identity());
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>, R extends Record> SelectOneAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull R record) {
        return new SelectOneAdapter<>(table, converter, r -> r.into(record));
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>> SelectOneAdapter<RS, C, T, Record> create(
        @NonNull T table, @NonNull C converter, @NonNull Collection<Field<?>> fields) {
        return new SelectOneAdapter<>(table, converter,
                                      r -> r.into(fields.stream().filter(Objects::nonNull).toArray(Field[]::new)));
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>, R> SelectOneAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull Class<R> outputClass) {
        return new SelectOneAdapter<>(table, converter, r -> r.into(outputClass));
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends Table<R>, R extends Record> SelectOneAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter) {
        return new SelectOneAdapter<>(table, converter, r -> r.into(table));
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>, R extends Record,
                      Z extends Table<R>> SelectOneAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull Z toTable) {
        return new SelectOneAdapter<>(table, converter, r -> r.into(toTable));
    }

}
