package io.github.zero88.jooq.vertx;

import java.util.Collection;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Table;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.adapter.SelectCountResultAdapter;
import io.github.zero88.jooq.vertx.adapter.SelectExistsResultAdapter;
import io.github.zero88.jooq.vertx.adapter.SelectListResultAdapter;
import io.github.zero88.jooq.vertx.adapter.SelectOneResultAdapter;
import io.github.zero88.jooq.vertx.converter.ResultSetConverter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class VertxDSLImpl<RS, C extends ResultSetConverter<RS>> implements VertxDSL<RS, C> {

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
    public <T extends TableLike<? extends Record>> SelectOneResultAdapter<RS, C, T, Record> fetchOne(@NonNull T table,
                                                                                                     @NonNull Collection<Field<?>> fields) {
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
    public <T extends TableLike<? extends Record>> SelectListResultAdapter<RS, C, T, Record> fetchMany(@NonNull T table,
                                                                                                       @NonNull Collection<Field<?>> fields) {
        return SelectListResultAdapter.create(table, converter, fields);
    }

    @Override
    public <T extends TableLike<? extends Record>, R> SelectListResultAdapter<RS, C, T, R> fetchMany(@NonNull T table,
                                                                                                     @NonNull Class<R> outputClass) {
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
