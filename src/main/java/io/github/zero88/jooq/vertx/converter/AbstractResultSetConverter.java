package io.github.zero88.jooq.vertx.converter;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.VertxJooqRecord;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractResultSetConverter<RS> implements ResultSetConverter<RS> {

    @Override
    public <T extends TableLike<? extends Record>> List<VertxJooqRecord<?>> convertVertxRecord(@NonNull RS resultSet,
                                                                                               T table) {
        return doConvert(resultSet, table, Function.identity());
    }

    @Override
    public <T extends Table<? extends Record>, R extends Record> List<R> convert(@NonNull RS resultSet,
                                                                                 @NonNull T table) {
        return doConvert(resultSet, table, r -> (R) r.into(table));
    }

    @Override
    public <T extends TableLike<? extends Record>, R> List<R> convert(@NonNull RS resultSet, T table,
                                                                      @NonNull Class<R> recordClass) {
        return doConvert(resultSet, table, r -> r.into(recordClass));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends TableLike<? extends Record>, R extends Record> List<R> convert(@NonNull RS resultSet, T table,
                                                                                     @NonNull R record) {
        return doConvert(resultSet, table, r -> (R) r.into(record.fields()));
    }

    @Override
    public <T extends TableLike<? extends Record>, R extends Record> List<R> convert(@NonNull RS resultSet, T table,
                                                                                     @NonNull Table<R> toTable) {
        return doConvert(resultSet, table, r -> r.into(toTable));
    }

    @Override
    public <T extends TableLike<? extends Record>> List<Record> convert(@NonNull RS resultSet, T table,
                                                                        @NonNull Collection<Field<?>> fields) {
        return doConvert(resultSet, table, r -> r.into(fields.stream().filter(Objects::nonNull).toArray(Field[]::new)));
    }

    protected abstract <T extends TableLike<? extends Record>, R> List<R> doConvert(@NonNull RS resultSet, T table,
                                                                                    @NonNull Function<VertxJooqRecord<?>, R> mapper);

}
