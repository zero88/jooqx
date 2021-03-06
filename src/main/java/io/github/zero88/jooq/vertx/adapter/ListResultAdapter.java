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

    private final BiFunction<SqlResultAdapter<RS, T, List<R>>, RS, List<R>> function;

    private ListResultAdapter(@NonNull T table, @NonNull ResultSetConverter<RS> converter,
                              @NonNull BiFunction<SqlResultAdapter<RS, T, List<R>>, RS, List<R>> function) {
        super(table, converter);
        this.function = function;
    }

    @Override
    public @NonNull List<R> convert(@NonNull RS resultSet) {
        return function.apply(this, resultSet);
    }

    public static <RS, T extends TableLike<? extends Record>> ListResultAdapter<RS, T, VertxJooqRecord<?>> createVertxRecord(
        @NonNull T table, @NonNull ResultSetConverter<RS> converter) {
        return new ListResultAdapter<>(table, converter,
                                       (adapter, rs) -> adapter.converter().convertVertxRecord(rs, adapter.table()));
    }

    public static <RS, T extends TableLike<? extends Record>, R extends Record> ListResultAdapter<RS, T, R> create(
        @NonNull T table, @NonNull ResultSetConverter<RS> converter, @NonNull R record) {
        return new ListResultAdapter<>(table, converter,
                                       (adapter, rs) -> adapter.converter().convert(rs, adapter.table(), record));
    }

    public static <RS, T extends TableLike<? extends Record>> ListResultAdapter<RS, T, Record> create(@NonNull T table,
                                                                                                      @NonNull ResultSetConverter<RS> converter,
                                                                                                      @NonNull Collection<Field<?>> fields) {
        return new ListResultAdapter<>(table, converter,
                                       (adapter, rs) -> adapter.converter().convert(rs, table, fields));
    }

    public static <RS, T extends TableLike<? extends Record>, R> ListResultAdapter<RS, T, R> create(@NonNull T table,
                                                                                                    @NonNull ResultSetConverter<RS> converter,
                                                                                                    @NonNull Class<R> outputClass) {
        return new ListResultAdapter<>(table, converter,
                                       (adapter, rs) -> adapter.converter().convert(rs, adapter.table(), outputClass));
    }

    //TODO fix it
    public static <RS, T extends Table<? extends Record>, R extends Record> ListResultAdapter<RS, T, R> create(
        @NonNull T table, @NonNull ResultSetConverter<RS> converter) {
        return new ListResultAdapter<>(table, converter,
                                       (adapter, rs) -> adapter.converter().convert(rs, adapter.table()));
    }

    public static <RS, T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> ListResultAdapter<RS, T, R> create(
        @NonNull T table, @NonNull ResultSetConverter<RS> converter, @NonNull Z toTable) {
        return new ListResultAdapter<>(table, converter,
                                       (adapter, rs) -> adapter.converter().convert(rs, adapter.table(), toTable));
    }

}
