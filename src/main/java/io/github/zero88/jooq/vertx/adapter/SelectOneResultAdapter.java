package io.github.zero88.jooq.vertx.adapter;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.VertxJooqRecord;
import io.github.zero88.jooq.vertx.adapter.HasStrategy.SelectOne;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;

import lombok.NonNull;

public final class SelectOneResultAdapter<RS, C extends ResultSetConverter<RS>, T extends TableLike<? extends Record>
                                             , R> extends InternalSelectResultAdapter<RS, C, T, R, R>
    implements SelectOne {

    protected SelectOneResultAdapter(@NonNull T table, @NonNull C converter,
                                     @NonNull BiFunction<SqlResultAdapter<RS, C, T, R>, RS, List<R>> function) {
        super(table, converter, function);
    }

    @Override
    public @NonNull R convert(@NonNull RS resultSet) {
        return getFunction().apply(this, resultSet).stream().findFirst().orElse(null);
    }

    public static <RS, C extends ResultSetConverter<RS>, T extends TableLike<? extends Record>> SelectOneResultAdapter<RS, C, T, VertxJooqRecord<?>> vertxRecord(
        @NonNull T table, @NonNull C converter) {
        return new SelectOneResultAdapter<>(table, converter,
                                            (a, rs) -> a.converter().convertVertxRecord(rs, a.table()));
    }

    public static <RS, C extends ResultSetConverter<RS>, T extends TableLike<? extends Record>, R extends Record> SelectOneResultAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull R record) {
        return new SelectOneResultAdapter<>(table, converter, (a, rs) -> a.converter().convert(rs, a.table(), record));
    }

    public static <RS, C extends ResultSetConverter<RS>, T extends TableLike<? extends Record>> SelectOneResultAdapter<RS, C, T, Record> create(
        @NonNull T table, @NonNull C converter, @NonNull Collection<Field<?>> fields) {
        return new SelectOneResultAdapter<>(table, converter, (a, rs) -> a.converter().convert(rs, table, fields));
    }

    public static <RS, C extends ResultSetConverter<RS>, T extends TableLike<? extends Record>, R> SelectOneResultAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull Class<R> outputClass) {
        return new SelectOneResultAdapter<>(table, converter,
                                            (a, rs) -> a.converter().convert(rs, a.table(), outputClass));
    }

    public static <RS, C extends ResultSetConverter<RS>, T extends Table<R>, R extends Record> SelectOneResultAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter) {
        return new SelectOneResultAdapter<>(table, converter, (a, rs) -> a.converter().convert(rs, a.table()));
    }

    public static <RS, C extends ResultSetConverter<RS>, T extends TableLike<? extends Record>, R extends Record,
                      Z extends Table<R>> SelectOneResultAdapter<RS, C, T, R> create(
        @NonNull T table, @NonNull C converter, @NonNull Z toTable) {
        return new SelectOneResultAdapter<>(table, converter, (a, rs) -> a.converter().convert(rs, a.table(), toTable));
    }

}
