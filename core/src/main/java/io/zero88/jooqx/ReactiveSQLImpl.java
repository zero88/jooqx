package io.zero88.jooqx;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

import org.jooq.Param;
import org.jooq.Record;

import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.ArrayTuple;
import io.zero88.jooqx.MiscImpl.DSLAI;
import io.zero88.jooqx.SQLImpl.SQLPQ;
import io.zero88.jooqx.SQLImpl.SQLRC;
import io.zero88.jooqx.adapter.RowConverterStrategy;
import io.zero88.jooqx.adapter.SelectStrategy;
import io.zero88.jooqx.datatype.SQLDataTypeRegistry;

import lombok.NonNull;

final class ReactiveSQLImpl {

    static final class ReactiveSQLPQ extends SQLPQ<Tuple> implements ReactiveSQLPreparedQuery {

        protected ArrayTuple doConvert(Map<String, Param<?>> params, SQLDataTypeRegistry registry,
                                       BiFunction<String, Param<?>, ?> queryValue) {
            final ArrayTuple bindValues = new ArrayTuple(params.size());
            params.entrySet()
                  .stream()
                  .filter(entry -> !entry.getValue().isInline())
                  .forEachOrdered(
                      etr -> bindValues.addValue(registry.toDatabaseType(etr.getKey(), etr.getValue(), queryValue)));
            return bindValues;
        }

    }


    static class ReactiveSQLRC extends SQLRC<RowSet<Row>> implements ReactiveSQLResultCollector {

        @Override
        public @NonNull <R extends Record, O> List<O> collect(@NonNull RowSet<Row> resultSet,
                                                              @NonNull RowConverterStrategy<R, O> strategy) {
            final List<O> records = new ArrayList<>();
            final RowIterator<Row> iterator = resultSet.iterator();
            if (strategy.strategy() == SelectStrategy.MANY) {
                iterator.forEachRemaining(row -> records.add(toRecord(strategy, row)));
            } else if (iterator.hasNext()) {
                records.add(toRecord(strategy, iterator.next()));
                warnManyResult(iterator.hasNext(), strategy.strategy());
            }
            return records;
        }

        private <R extends Record, O> O toRecord(@NonNull RowConverterStrategy<R, O> strategy, @NonNull Row row) {
            return IntStream.range(0, row.size())
                            .mapToObj(row::getColumnName)
                            .map(strategy::lookupField)
                            .filter(Objects::nonNull)
                            .collect(strategy.createCollector(f -> row.getValue(f.getName())));
        }

    }


    static final class ReactiveSQLRBC extends ReactiveSQLRC implements ReactiveSQLBatchCollector {

        @Override
        public @NonNull <R extends Record, O> List<O> collect(@NonNull RowSet<Row> resultSet,
                                                              @NonNull RowConverterStrategy<R, O> strategy) {
            final List<O> records = new ArrayList<>();
            while (resultSet != null) {
                final List<O> rows = super.collect(resultSet, strategy);
                if (!rows.isEmpty()) {
                    records.add(rows.get(0));
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


    static final class ReactiveDSLAdapter extends DSLAI<RowSet<Row>, ReactiveSQLResultCollector>
        implements ReactiveDSL {

        ReactiveDSLAdapter() { super(new ReactiveSQLRC()); }

    }

}
