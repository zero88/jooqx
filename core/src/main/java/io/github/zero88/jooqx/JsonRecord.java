package io.github.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.jooq.TableLike;
import org.jooq.TableRecord;

import io.vertx.core.json.JsonObject;

/**
 * Represents for an intermediate record between Vertx ResultSet and actual jOOQ Record and does support serialize to
 * {@code JsonObject}
 *
 * @param <R> Type of jOOQ record
 * @see TableRecord
 * @since 1.0.0
 */
public interface JsonRecord<R extends TableRecord<R>> extends TableRecord<R> {

    JsonObject toJson();

    static <R extends TableRecord<R>> JsonRecord<R> create(@NotNull TableLike<R> table) {
        return new MiscImpl.JsonRecordImpl<>(table.asTable());
    }

}
