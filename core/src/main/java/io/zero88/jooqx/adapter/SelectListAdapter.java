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
import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.adapter.SQLResultAdapter.SelectMany;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.NonNull;

/**
 * Select list result adapter that returns list of output
 *
 * @see SQLResultAdapter
 * @see SelectMany
 * @since 1.0.0
 */
public final class SelectListAdapter<RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>, O>
    extends SelectResultInternal<RS, C, T, O, List<O>> implements SelectMany {

    private SelectListAdapter(@NonNull T table, @NonNull C converter, @NonNull Function<JsonRecord<?>, O> mapper) {
        super(table, converter, mapper);
    }

    @Override
    public @NonNull List<O> collect(@NonNull RS resultSet, @NonNull SQLDataTypeRegistry registry) {
        return converter().collect(resultSet, converterStrategy(registry))
                          .stream()
                          .map(mapper())
                          .collect(Collectors.toList());
    }

    public static <RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>> SelectListAdapter<RS,
                                                                                                                     C, T, JsonRecord<?>> jsonRecord(
        @NonNull T table, @NonNull C converter) {
        return new SelectListAdapter<>(table, converter, Function.identity());
    }

    public static <RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>, R extends Record> SelectListAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull R record) {
        return new SelectListAdapter<>(table, converter, r -> r.into(record));
    }

    public static <RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>> SelectListAdapter<RS,
                                                                                                                     C, T, Record> create(
        @NonNull T table, @NonNull C converter, @NonNull Collection<Field<?>> fields) {
        return new SelectListAdapter<>(table, converter,
                                       r -> r.into(fields.stream().filter(Objects::nonNull).toArray(Field[]::new)));
    }

    public static <RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>, R> SelectListAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull Class<R> outputClass) {
        return new SelectListAdapter<>(table, converter, r -> r.into(outputClass));
    }

    public static <RS, C extends SQLResultCollector<RS>, T extends Table<R>, R extends Record> SelectListAdapter<RS,
                                                                                                                    C
                                                                                                                    ,
                                                                                                                    T
                                                                                                                    ,
                                                                                                                    R> create(
        @NonNull T table, @NonNull C converter) {
        return new SelectListAdapter<>(table, converter, r -> r.into(table));
    }

    public static <RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>, R extends Record,
                      Z extends Table<R>> SelectListAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull Z toTable) {
        return new SelectListAdapter<>(table, converter, r -> r.into(toTable));
    }

}
