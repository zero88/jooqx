package io.zero88.jooqx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.impl.CustomRecord;

import io.vertx.core.json.JsonObject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

final class MiscImpl {

    @Getter
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    static class BatchResultImpl implements BatchResult {

        private final int total;
        private final int successes;

        static BatchResult create(int total, int successes) {
            return new BatchResultImpl(total, successes);
        }

        static <R> BatchReturningResult<R> create(int total, List<R> results) {
            return new BatchRRI<>(total, Optional.ofNullable(results).orElseGet(ArrayList::new));
        }

    }


    static final class BatchRRI<R> extends BatchResultImpl implements BatchReturningResult<R> {

        @Getter
        @NonNull
        private final List<R> records;

        private BatchRRI(int total, @NonNull List<R> rs) {
            super(total, rs.size());
            this.records = rs;
        }

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
