package io.github.zero88.jooq.vertx.record;

import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.TableRecord;

import io.vertx.core.json.JsonObject;

import lombok.NonNull;

public interface VertxJooqRecord<R extends TableRecord<R>> extends TableRecord<R> {

    JsonObject toJson();

    static <R extends TableRecord<R>> VertxJooqRecord<R> create(@NonNull TableLike<R> table) {
        return new VertxJooqRecordImpl<>((Table<R>) table);
    }

}
