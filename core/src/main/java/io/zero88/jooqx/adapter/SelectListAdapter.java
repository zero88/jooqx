package io.zero88.jooqx.adapter;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.zero88.jooqx.JsonRecord;
import io.zero88.jooqx.SQLResultConverter;
import io.zero88.jooqx.adapter.HasStrategy.SelectMany;
import io.zero88.jooqx.adapter.SQLResultAdapterImpl.InternalSelectResultAdapter;

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

    private SelectListAdapter(@NonNull T table, @NonNull C converter,
                              @NonNull BiFunction<SQLResultAdapter<R, C, T, List<O>>, R, List<O>> function) {
        super(table, converter, function);
    }

    @Override
    public @NonNull List<O> convert(@NonNull R resultSet) {
        return getFunction().apply(this, resultSet);
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>> SelectListAdapter<RS, C, T, JsonRecord<?>> jsonRecord(
        @NonNull T table, @NonNull C converter) {
        return new SelectListAdapter<>(table, converter, (a, rs) -> a.converter().convertJsonRecord(rs, a.table()));
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>, R extends Record> SelectListAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull R record) {
        return new SelectListAdapter<>(table, converter, (a, rs) -> a.converter().convert(rs, a.table(), record));
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>> SelectListAdapter<RS,
                                                                                                                     C, T, Record> create(
        @NonNull T table, @NonNull C converter, @NonNull Collection<Field<?>> fields) {
        return new SelectListAdapter<>(table, converter, (a, rs) -> a.converter().convert(rs, a.table(), fields));
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>, R> SelectListAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull Class<R> outputClass) {
        return new SelectListAdapter<>(table, converter, (a, rs) -> a.converter().convert(rs, a.table(), outputClass));
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends Table<R>, R extends Record> SelectListAdapter<RS, C , T , R> create(
        @NonNull T table, @NonNull C converter) {
        return new SelectListAdapter<>(table, converter, (a, rs) -> a.converter().convert(rs, a.table()));
    }

    public static <RS, C extends SQLResultConverter<RS>, T extends TableLike<? extends Record>, R extends Record,
                      Z extends Table<R>> SelectListAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull Z toTable) {
        return new SelectListAdapter<>(table, converter, (a, rs) -> a.converter().convert(rs, a.table(), toTable));
    }

}
