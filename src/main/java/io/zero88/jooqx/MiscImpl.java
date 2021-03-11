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

import io.zero88.jooqx.adapter.SelectCountResultAdapter;
import io.zero88.jooqx.adapter.SelectExistsResultAdapter;
import io.zero88.jooqx.adapter.SelectListResultAdapter;
import io.zero88.jooqx.adapter.SelectOneResultAdapter;
import io.vertx.core.json.JsonObject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

final class MiscImpl {

    @RequiredArgsConstructor
    static class DSLAI<RS, C extends SQLResultSetConverter<RS>> implements DSLAdapter<RS, C> {

        @NonNull
        private final C converter;

        @Override
        public SelectCountResultAdapter<RS, C> fetchCount(@NonNull TableLike<Record1<Integer>> table) {
            return SelectCountResultAdapter.count(table, converter);
        }

        @Override
        public SelectExistsResultAdapter<RS, C> fetchExists(@NonNull TableLike<Record1<Integer>> table) {
            return SelectExistsResultAdapter.exist(table, converter);
        }

        @Override
        public <T extends TableLike<? extends Record>> SelectOneResultAdapter<RS, C, T, JsonRecord<?>> fetchJsonRecord(
            @NonNull T table) {
            return SelectOneResultAdapter.jsonRecord(table, converter);
        }

        @Override
        public <T extends TableLike<? extends Record>, R extends Record> SelectOneResultAdapter<RS, C, T, R> fetchOne(
            @NonNull T table, @NonNull R record) {
            return SelectOneResultAdapter.create(table, converter, record);
        }

        @Override
        public <T extends TableLike<? extends Record>> SelectOneResultAdapter<RS, C, T, Record> fetchOne(
            @NonNull T table, @NonNull Collection<Field<?>> fields) {
            return SelectOneResultAdapter.create(table, converter, fields);
        }

        @Override
        public <T extends TableLike<? extends Record>, R> SelectOneResultAdapter<RS, C, T, R> fetchOne(@NonNull T table,
                                                                                                       @NonNull Class<R> outputClass) {
            return SelectOneResultAdapter.create(table, converter, outputClass);
        }

        @Override
        public <T extends Table<R>, R extends Record> SelectOneResultAdapter<RS, C, T, R> fetchOne(@NonNull T table) {
            return SelectOneResultAdapter.create(table, converter);
        }

        @Override
        public <T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> SelectOneResultAdapter<RS, C, T, R> fetchOne(
            @NonNull T table, @NonNull Z toTable) {
            return SelectOneResultAdapter.create(table, converter, toTable);
        }

        @Override
        public <T extends TableLike<? extends Record>> SelectListResultAdapter<RS, C, T, JsonRecord<?>> fetchJsonRecords(
            @NonNull T table) {
            return SelectListResultAdapter.jsonRecord(table, converter);
        }

        @Override
        public <T extends TableLike<? extends Record>, R extends Record> SelectListResultAdapter<RS, C, T, R> fetchMany(
            @NonNull T table, @NonNull R record) {
            return SelectListResultAdapter.create(table, converter, record);
        }

        @Override
        public <T extends TableLike<? extends Record>> SelectListResultAdapter<RS, C, T, Record> fetchMany(
            @NonNull T table, @NonNull Collection<Field<?>> fields) {
            return SelectListResultAdapter.create(table, converter, fields);
        }

        @Override
        public <T extends TableLike<? extends Record>, R> SelectListResultAdapter<RS, C, T, R> fetchMany(
            @NonNull T table, @NonNull Class<R> outputClass) {
            return SelectListResultAdapter.create(table, converter, outputClass);
        }

        @Override
        public <T extends Table<R>, R extends Record> SelectListResultAdapter<RS, C, T, R> fetchMany(@NonNull T table) {
            return SelectListResultAdapter.create(table, converter);
        }

        @Override
        public <T extends TableLike<? extends Record>, R extends Record, Z extends Table<R>> SelectListResultAdapter<RS, C, T, R> fetchMany(
            @NonNull T table, @NonNull Z toTable) {
            return SelectListResultAdapter.create(table, converter, toTable);
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
