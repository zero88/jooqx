package io.github.zero88.jooq.vertx.adapter;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.VertxJooqRecord;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;

import lombok.NonNull;

public final class ListResultAdapter<RS, T extends TableLike<? extends Record>, R>
    extends AbstractSqlResultAdapter<RS, T, List<R>> {

    private final BiFunction<ResultSetConverter<RS, T>, RS, List<R>> function;

    private ListResultAdapter(@NonNull ResultSetConverter<RS, T> converter,
                              @NonNull BiFunction<ResultSetConverter<RS, T>, RS, List<R>> function) {
        super(converter);
        this.function = function;
    }

    @Override
    public @NonNull List<R> convert(@NonNull RS resultSet) {
        return function.apply(converter(), resultSet);
    }

    public static <RS, T extends TableLike<? extends Record>> ListResultAdapter<RS, T, VertxJooqRecord<?>> create(
        @NonNull ResultSetConverter<RS, T> converter) {
        return new ListResultAdapter<>(converter, ResultSetConverter::convert);
    }

    public static <RS, T extends TableLike<? extends Record>, R extends Record> ListResultAdapter<RS, T, R> create(
        @NonNull ResultSetConverter<RS, T> converter, @NonNull R record) {
        return new ListResultAdapter<>(converter, (c, rs) -> c.convert(rs, record));
    }

    public static <RS, T extends TableLike<? extends Record>> ListResultAdapter<RS, T, Record> create(
        @NonNull ResultSetConverter<RS, T> converter, @NonNull Collection<Field<?>> fields) {
        return new ListResultAdapter<>(converter, (c, rs) -> c.convert(rs, fields));
    }

    public static <RS, T extends TableLike<? extends Record>, R> ListResultAdapter<RS, T, R> create(
        @NonNull ResultSetConverter<RS, T> converter, @NonNull Class<R> outputClass) {
        return new ListResultAdapter<>(converter, (c, rs) -> c.convert(rs, outputClass));
    }

    public static <RS, T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> ListResultAdapter<RS, T, R> create(
        @NonNull ResultSetConverter<RS, T> converter, @NonNull Z table) {
        return new ListResultAdapter<>(converter, (c, rs) -> c.convert(rs, table));
    }

}
