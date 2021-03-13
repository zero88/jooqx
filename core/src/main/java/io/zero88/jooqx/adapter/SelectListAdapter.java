package io.zero88.jooqx.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.zero88.jooqx.JsonRecord;
import io.zero88.jooqx.SQLResultConverter;
import io.zero88.jooqx.adapter.HasStrategy.SelectMany;
import io.zero88.jooqx.adapter.SQLResultAdapterImpl.InternalSelectResultAdapter;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.NonNull;

/**
 * Select list result adapter that defines output in {@code List} type
 *
 * @param <R> Type of Vertx Result set
 * @param <C> Type of result set converter
 * @param <T> Type of jOOQ Table
 * @param <O> Type of an expectation output
 * @since 1.0.0
 */
public final class SelectListAdapter<R, C extends SQLResultConverter<R>, T extends TableLike<? extends Record>, O>
    extends InternalSelectResultAdapter<R, C, T, O, List<O>> implements SelectMany {

    private SelectListAdapter(@NonNull T table, @NonNull C converter, @NonNull Function<JsonRecord<?>, O> mapper) {
        super(table, converter, mapper);
    }

    @Override
    public @NonNull List<O> collect(@NonNull R resultSet, @NonNull SQLDataTypeRegistry registry) {
        return converter().collect(resultSet, createConverterStrategy(registry))
                          .stream()
                          .map(getMapper())
                          .collect(Collectors.toList());
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>> SelectListAdapter<RS,
                                                                                                                     C, T, JsonRecord<?>> jsonRecord(
        @NonNull T table, @NonNull C converter) {
        return new SelectListAdapter<>(table, converter, Function.identity());
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>, R extends Record> SelectListAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull R record) {
        return new SelectListAdapter<>(table, converter, r -> r.into(record));
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>> SelectListAdapter<RS,
                                                                                                                     C, T, Record> create(
        @NonNull T table, @NonNull C converter, @NonNull Collection<Field<?>> fields) {
        return new SelectListAdapter<>(table, converter,
                                       r -> r.into(fields.stream().filter(Objects::nonNull).toArray(Field[]::new)));
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>, R> SelectListAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull Class<R> outputClass) {
        return new SelectListAdapter<>(table, converter, r -> r.into(outputClass));
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends Table<R>, R extends Record> SelectListAdapter<RS,
                                                                                                                    C
                                                                                                                    ,
                                                                                                                    T
                                                                                                                    ,
                                                                                                                    R> create(
        @NonNull T table, @NonNull C converter) {
        return new SelectListAdapter<>(table, converter, r -> r.into(table));
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>, R extends Record,
                      Z extends Table<R>> SelectListAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull Z toTable) {
        return new SelectListAdapter<>(table, converter, r -> r.into(toTable));
    }

}
