package io.github.zero88.jooq.vertx;

import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.adapter.SelectCountResultAdapter;
import io.github.zero88.jooq.vertx.adapter.SelectExistsResultAdapter;
import io.github.zero88.jooq.vertx.adapter.SelectOneResultAdapter;
import io.github.zero88.jooq.vertx.converter.ReactiveResultSetConverter;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;

import lombok.NonNull;

public final class VertxReactiveDSL {

    public static SelectCountResultAdapter<RowSet<Row>, ReactiveResultSetConverter> count(
        @NonNull TableLike<Record1<Integer>> table) {
        return SelectCountResultAdapter.count(table, new ReactiveResultSetConverter());
    }

    public static SelectExistsResultAdapter<RowSet<Row>, ReactiveResultSetConverter> exist(
        @NonNull TableLike<Record1<Integer>> table) {
        return SelectExistsResultAdapter.exist(table, new ReactiveResultSetConverter());
    }

    public static <T extends TableLike<? extends Record>> SelectOneResultAdapter<RowSet<Row>,
                                                                                    ReactiveResultSetConverter, T,
                                                                                    VertxJooqRecord<?>> selectOne(
        @NonNull T table) {
        return SelectOneResultAdapter.vertxRecord(table, new ReactiveResultSetConverter());
    }

}
