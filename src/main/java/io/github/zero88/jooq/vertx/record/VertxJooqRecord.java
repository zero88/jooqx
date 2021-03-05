package io.github.zero88.jooq.vertx.record;

import org.jooq.Table;
import org.jooq.TableLike;
import org.jooq.TableRecord;

import io.vertx.core.json.JsonObject;

import lombok.NonNull;

/**
 * Represents for an intermediate record between Vertx ResultSet and actual jOOQ Record and does support JsonObject
 *
 * @param <R> Type of jOOQ record
 * @see TableRecord
 * @since 1.0.0
 */
public interface VertxJooqRecord<R extends TableRecord<R>> extends TableRecord<R> {

    JsonObject toJson();

    static <R extends TableRecord<R>> VertxJooqRecord<R> create(@NonNull TableLike<R> table) {
        return new VertxJooqRecordImpl<>((Table<R>) table);
    }

}
