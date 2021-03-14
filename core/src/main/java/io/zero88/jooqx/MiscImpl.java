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
import io.zero88.jooqx.adapter.SelectCountAdapter;
import io.zero88.jooqx.adapter.SelectExistsAdapter;
import io.zero88.jooqx.adapter.SelectListAdapter;
import io.zero88.jooqx.adapter.SelectOneAdapter;

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
        public SelectCountAdapter<RS, C> fetchCount(@NonNull TableLike<Record1<Integer>> table) {
            return SelectCountAdapter.count(table, converter);
        }

        @Override
        public SelectExistsAdapter<RS, C> fetchExists(@NonNull TableLike<Record1<Integer>> table) {
            return SelectExistsAdapter.exist(table, converter);
        }

        @Override
        public <T extends TableLike<? extends Record>> SelectOneAdapter<RS, C, T, JsonRecord<?>, JsonRecord<?>> fetchJsonRecord(
            @NonNull T table) {
            return new SelectOneAdapter<>(table, converter, SQLResultAdapter.byJson());
        }

        @Override
        public <T extends TableLike<? extends Record>, R extends TableRecord<R>> SelectOneAdapter<RS, C, T, R, R> fetchOne(
            @NonNull T table, @NonNull R record) {
            return new SelectOneAdapter<>(table, converter, SQLResultAdapter.byRecord(record));
        }

        @Override
        public <T extends TableLike<? extends Record>> SelectOneAdapter<RS, C, T, Record, Record> fetchOne(
            @NonNull T table, @NonNull Collection<Field<?>> fields) {
            return new SelectOneAdapter<>(table, converter, SQLResultAdapter.byFields(fields));
        }

        @Override
        public <T extends TableLike<? extends Record>, R> SelectOneAdapter<RS, C, T, JsonRecord<?>, R> fetchOne(
            @NonNull T table, @NonNull Class<R> outputClass) {
            return new SelectOneAdapter<>(table, converter, SQLResultAdapter.byClass(outputClass));
        }

        @Override
        public <T extends Table<R>, R extends Record> SelectOneAdapter<RS, C, T, R, R> fetchOne(@NonNull T table) {
            return new SelectOneAdapter<>(table, converter, SQLResultAdapter.byTable(table));
        }

        @Override
        public <T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> SelectOneAdapter<RS, C, T, R, R> fetchOne(
            @NonNull T table, @NonNull Z toTable) {
            return new SelectOneAdapter<>(table, converter, SQLResultAdapter.byTable(toTable));
        }

        @Override
        public <T extends TableLike<? extends Record>> SelectListAdapter<RS, C, T, JsonRecord<?>, JsonRecord<?>> fetchJsonRecords(
            @NonNull T table) {
            return new SelectListAdapter<>(table, converter, SQLResultAdapter.byJson());
        }

        @Override
        public <T extends TableLike<? extends Record>, R extends TableRecord<R>> SelectListAdapter<RS, C, T, R, R> fetchMany(
            @NonNull T table, @NonNull R record) {
            return new SelectListAdapter<>(table, converter, SQLResultAdapter.byRecord(record));
        }

        @Override
        public <T extends TableLike<? extends Record>> SelectListAdapter<RS, C, T, Record, Record> fetchMany(
            @NonNull T table, @NonNull Collection<Field<?>> fields) {
            return new SelectListAdapter<>(table, converter, SQLResultAdapter.byFields(fields));
        }

        @Override
        public <T extends TableLike<? extends Record>, R> SelectListAdapter<RS, C, T, JsonRecord<?>, R> fetchMany(
            @NonNull T table, @NonNull Class<R> outputClass) {
            return new SelectListAdapter<>(table, converter, SQLResultAdapter.byClass(outputClass));
        }

        @Override
        public <T extends Table<R>, R extends Record> SelectListAdapter<RS, C, T, R, R> fetchMany(@NonNull T table) {
            return new SelectListAdapter<>(table, converter, SQLResultAdapter.byTable(table));
        }

        @Override
        public <T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> SelectListAdapter<RS, C, T, R, R> fetchMany(
            @NonNull T table, @NonNull Z toTable) {
            return new SelectListAdapter<>(table, converter, SQLResultAdapter.byTable(toTable));
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
