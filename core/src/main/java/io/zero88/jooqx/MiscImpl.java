package io.zero88.jooqx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.TableRecord;
import org.jooq.impl.CustomRecord;

import io.vertx.core.json.JsonObject;
import io.zero88.jooqx.adapter.SQLResultAdapter;
import io.zero88.jooqx.adapter.SelectCount;
import io.zero88.jooqx.adapter.SelectExists;
import io.zero88.jooqx.adapter.SelectList;
import io.zero88.jooqx.adapter.SelectOne;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

final class MiscImpl {

    @RequiredArgsConstructor
    static class DSLAI<RS, C extends SQLResultCollector<RS>> implements DSLAdapter<RS, C> {

        @NonNull
        private final C converter;

        @Override
        public SelectCount<RS, C> fetchCount(@NonNull TableLike<Record1<Integer>> table) {
            return SelectCount.count(table, converter);
        }

        @Override
        public SelectExists<RS, C> fetchExists(@NonNull TableLike<Record1<Integer>> table) {
            return SelectExists.exist(table, converter);
        }

        @Override
        public <T extends TableLike<? extends Record>> SelectOne<RS, C, T, JsonRecord<?>, JsonRecord<?>> fetchJsonRecord(
            @NonNull T table) {
            return new SelectOne<>(table, converter, SQLResultAdapter.byJson());
        }

        @Override
        public <T extends TableLike<? extends Record>, R extends TableRecord<R>> SelectOne<RS, C, T, R, R> fetchOne(
            @NonNull T table, @NonNull R record) {
            return new SelectOne<>(table, converter, SQLResultAdapter.byRecord(record));
        }

        @Override
        public <T extends TableLike<? extends Record>> SelectOne<RS, C, T, Record, Record> fetchOne(
            @NonNull T table, @NonNull Collection<Field<?>> fields) {
            return new SelectOne<>(table, converter, SQLResultAdapter.byFields(fields));
        }

        @Override
        public <T extends TableLike<? extends Record>, R> SelectOne<RS, C, T, JsonRecord<?>, R> fetchOne(
            @NonNull T table, @NonNull Class<R> outputClass) {
            return new SelectOne<>(table, converter, SQLResultAdapter.byClass(outputClass));
        }

        @Override
        public <T extends Table<R>, R extends Record> SelectOne<RS, C, T, R, R> fetchOne(@NonNull T table) {
            return new SelectOne<>(table, converter, SQLResultAdapter.byTable(table));
        }

        @Override
        public <T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> SelectOne<RS, C, T, R, R> fetchOne(
            @NonNull T table, @NonNull Z toTable) {
            return new SelectOne<>(table, converter, SQLResultAdapter.byTable(toTable));
        }

        @Override
        public <T extends TableLike<? extends Record>> SelectList<RS, C, T, JsonRecord<?>, JsonRecord<?>> fetchJsonRecords(
            @NonNull T table) {
            return new SelectList<>(table, converter, SQLResultAdapter.byJson());
        }

        @Override
        public <T extends TableLike<? extends Record>, R extends TableRecord<R>> SelectList<RS, C, T, R, R> fetchMany(
            @NonNull T table, @NonNull R record) {
            return new SelectList<>(table, converter, SQLResultAdapter.byRecord(record));
        }

        @Override
        public <T extends TableLike<? extends Record>> SelectList<RS, C, T, Record, Record> fetchMany(
            @NonNull T table, @NonNull Collection<Field<?>> fields) {
            return new SelectList<>(table, converter, SQLResultAdapter.byFields(fields));
        }

        @Override
        public <T extends TableLike<? extends Record>, R> SelectList<RS, C, T, JsonRecord<?>, R> fetchMany(
            @NonNull T table, @NonNull Class<R> outputClass) {
            return new SelectList<>(table, converter, SQLResultAdapter.byClass(outputClass));
        }

        @Override
        public <T extends Table<R>, R extends Record> SelectList<RS, C, T, R, R> fetchMany(@NonNull T table) {
            return new SelectList<>(table, converter, SQLResultAdapter.byTable(table));
        }

        @Override
        public <T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> SelectList<RS, C, T, JsonRecord<?>, R> fetchMany(
            @NonNull T table, @NonNull Z toTable) {
            return new SelectList<>(table, converter, SQLResultAdapter.byJson().andThen(r -> r.into(toTable)));
        }

    }


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
