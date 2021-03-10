package io.github.zero88.jooq.vertx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jooq.Field;
import org.jooq.Param;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.SQLImpl.SQLPQ;
import io.github.zero88.jooq.vertx.SQLImpl.SQLRSC;
import io.github.zero88.jooq.vertx.adapter.SelectStrategy;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.ArrayTuple;

import lombok.NonNull;

final class ReactiveSQLImpl {

    public static ReactiveSQLResultBatchConverter resultBatchConverter() {
        return new ReactiveSQLRBC();
    }

    static final class ReactiveSQLPQ extends SQLPQ<Tuple> implements ReactiveSQLPreparedQuery {

        protected ArrayTuple doConvert(Map<String, Param<?>> params, BiFunction<String, Param<?>, ?> queryValue) {
            final ArrayTuple bindValues = new ArrayTuple(params.size());
            params.entrySet()
                  .stream()
                  .filter(entry -> !entry.getValue().isInline())
                  .forEachOrdered(etr -> bindValues.addValue(toDatabaseType(etr.getKey(), etr.getValue(), queryValue)));
            return bindValues;
        }

    }


    static class ReactiveSQLRSC extends SQLRSC<RowSet<Row>> implements ReactiveSQLResultConverter {

        @Override
        protected <T extends TableLike<? extends Record>, R> List<R> doConvert(@NonNull RowSet<Row> resultSet, T table,
                                                                               @NonNull Function<JsonRecord<?>, R> mapper) {
            final Map<String, Field<?>> fieldMap = table.fieldStream()
                                                        .collect(Collectors.toMap(Field::getName, Function.identity()));
            final List<R> records = new ArrayList<>();
            final RowIterator<Row> iterator = resultSet.iterator();
            if (strategy == SelectStrategy.MANY) {
                iterator.forEachRemaining(row -> records.add(mapper.apply(toRecord(table, row, fieldMap::get))));
            } else if (iterator.hasNext()) {
                records.add(mapper.apply(toRecord(table, iterator.next(), fieldMap::get)));
                warnManyResult(iterator.hasNext());
            }
            return records;
        }

        @SuppressWarnings( {"unchecked", "rawtypes"})
        private JsonRecord<?> toRecord(@NonNull TableLike table, @NonNull Row row,
                                       @NonNull Function<String, Field<?>> lookupField) {
            JsonRecord<?> record = JsonRecord.create((Table<JsonRecord>) table);
            IntStream.range(0, row.size())
                     .mapToObj(row::getColumnName)
                     .map(lookupField)
                     .filter(Objects::nonNull)
                     .forEach(f -> record.set((Field<Object>) f, row.get(f.getType(), f.getName())));
            return record;
        }

    }


    static final class ReactiveSQLRBC extends ReactiveSQLRSC implements ReactiveSQLResultBatchConverter {

        @Override
        protected <T extends TableLike<? extends Record>, R> List<R> doConvert(@NonNull RowSet<Row> resultSet,
                                                                               @NonNull T table,
                                                                               @NonNull Function<JsonRecord<?>, R> mapper) {
            final List<R> records = new ArrayList<>();
            while (resultSet != null) {
                final List<R> rs = super.doConvert(resultSet, table, mapper);
                if (!rs.isEmpty()) {
                    records.add(rs.get(0));
                }
                resultSet = resultSet.next();
            }
            return records;
        }

        @Override
        public int batchResultSize(@NonNull RowSet<Row> batchResult) {
            final int[] count = new int[] {0};
            while (batchResult != null) {
                count[0]++;
                batchResult = batchResult.next();
            }
            return count[0];
        }

    }


    static final class ReactiveDSLAI extends MiscImpl.DSLAdapterImpl<RowSet<Row>, ReactiveSQLResultConverter>
        implements ReactiveDSLAdapter {

        ReactiveDSLAI() { super(new ReactiveSQLRSC()); }

        ReactiveDSLAI(@NonNull ReactiveSQLResultConverter converter) {
            super(converter);
        }

    }

}
