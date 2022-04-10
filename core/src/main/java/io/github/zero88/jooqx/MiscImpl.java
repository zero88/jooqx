package io.github.zero88.jooqx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.impl.CustomRecord;

import io.vertx.core.json.JsonObject;

final class MiscImpl {

    static class BatchResultImpl implements BatchResult {

        private final int total;
        private final int successes;

        private BatchResultImpl(int total, int successes) {
            this.total     = total;
            this.successes = successes;
        }

        static BatchResult create(int total, int successes) {
            return new BatchResultImpl(total, successes);
        }

        static <R> BatchReturningResult<R> create(int total, List<R> results) {
            return new BatchReturningResultImpl<>(total, Optional.ofNullable(results).orElseGet(ArrayList::new));
        }

        @Override
        public int getTotal() { return total; }

        @Override
        public int getSuccesses() { return successes; }

    }


    static final class BatchReturningResultImpl<R> extends BatchResultImpl implements BatchReturningResult<R> {

        @NotNull
        private final List<R> records;

        private BatchReturningResultImpl(int total, @NotNull List<R> rs) {
            super(total, rs.size());
            this.records = rs;
        }

        @NotNull
        @Override
        public List<R> getRecords() { return records; }

    }


    static final class JsonRecordImpl<R extends TableRecord<R>> extends CustomRecord<R> implements JsonRecord<R> {

        public JsonRecordImpl(Table<R> table) {
            super(table);
        }

        public JsonObject toJson() {
            final JsonObject json = new JsonObject();
            Arrays.stream(this.fields()).forEach(f -> json.put(f.getName(), f.get(this)));
            return json;
        }

    }

}
