package io.zero88.jooqx.adapter;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.zero88.jooqx.JsonRecord;
import io.zero88.jooqx.SQLResultCollector;
import io.zero88.jooqx.adapter.SQLResultAdapter.SelectOne;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.NonNull;

/**
 * Select one result adapter that returns only one row
 *
 * @see SQLResultAdapter
 * @see SelectOne
 * @since 1.0.0
 */
public final class SelectOneAdapter<RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>, O>
    extends SelectResultInternal<RS, C, T, O, O> implements SelectOne {

    protected SelectOneAdapter(@NonNull T table, @NonNull C converter, @NonNull Function<JsonRecord<?>, O> mapper) {
        super(table, converter, mapper);
    }

    @Override
    public O collect(@NonNull RS resultSet, @NonNull SQLDataTypeRegistry registry) {
        return converter().collect(resultSet, converterStrategy(registry))
                          .stream()
                          .findFirst()
                          .map(mapper())
                          .orElse(null);
    }

    public static <RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>> SelectOneAdapter<RS,
                                                                                                                    C
                                                                                                                    ,
                                                                                                                    T
                                                                                                                    ,
                                                                                                                    JsonRecord<?>> jsonRecord(
        @NonNull T table, @NonNull C converter) {
        return new SelectOneAdapter<>(table, converter, Function.identity());
    }

    public static <RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>, R extends Record> SelectOneAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull R record) {
        return new SelectOneAdapter<>(table, converter, r -> r.into(record));
    }

    public static <RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>> SelectOneAdapter<RS,
                                                                                                                    C
                                                                                                                    ,
                                                                                                                    T
                                                                                                                    ,
                                                                                                                    Record> create(
        @NonNull T table, @NonNull C converter, @NonNull Collection<Field<?>> fields) {
        return new SelectOneAdapter<>(table, converter,
                                      r -> r.into(fields.stream().filter(Objects::nonNull).toArray(Field[]::new)));
    }

    public static <RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>, R> SelectOneAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull Class<R> outputClass) {
        return new SelectOneAdapter<>(table, converter, r -> r.into(outputClass));
    }

    public static <RS, C extends SQLResultCollector<RS>, T extends Table<R>, R extends Record> SelectOneAdapter<RS, C
                                                                                                                   ,
                                                                                                                   T,
                                                                                                                   R> create(
        @NonNull T table, @NonNull C converter) {
        return new SelectOneAdapter<>(table, converter, r -> r.into(table));
    }

    public static <RS, C extends SQLResultCollector<RS>, T extends TableLike<? extends Record>, R extends Record,
                      Z extends Table<R>> SelectOneAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull Z toTable) {
        return new SelectOneAdapter<>(table, converter, r -> r.into(toTable));
    }

}
