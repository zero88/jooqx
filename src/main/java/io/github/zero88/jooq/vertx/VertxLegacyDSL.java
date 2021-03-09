package io.github.zero88.jooq.vertx;

import org.jooq.Record1;
import org.jooq.TableLike;

import io.github.zero88.jooq.vertx.adapter.SelectCountResultAdapter;
import io.github.zero88.jooq.vertx.adapter.SelectExistsResultAdapter;
import io.github.zero88.jooq.vertx.converter.LegacyResultSetConverter;
import io.vertx.ext.sql.ResultSet;

import lombok.NonNull;

public final class VertxLegacyDSL {

    public static SelectCountResultAdapter<ResultSet, LegacyResultSetConverter> count(
        @NonNull TableLike<Record1<Integer>> table) {
        return SelectCountResultAdapter.count(table, new LegacyResultSetConverter());
    }

    public static SelectExistsResultAdapter<ResultSet, LegacyResultSetConverter> exist(
        @NonNull TableLike<Record1<Integer>> table) {
        return SelectExistsResultAdapter.exist(table, new LegacyResultSetConverter());
    }

}
