package io.github.zero88.jooqx;

import org.jetbrains.annotations.NotNull;
import org.jooq.JSONFormat;
import org.jooq.Record;
import org.jooq.TableLike;

import io.vertx.codegen.annotations.CacheReturn;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.json.JsonCodec;

/**
 * Represents for a wrapper of jOOQ Record and does support serialize to {@code JsonObject}
 *
 * @param <R> Type of jOOQ record
 * @apiNote breaking changes since v2.0.0
 * @since 1.0.0
 */
public interface JsonRecord<R extends Record> {

    /**
     * @return record
     * @since 2.0.0
     */
    R record();

    @CacheReturn
    JsonObject toJson();

    /**
     * Covert to json object by {@link JSONFormat}
     *
     * @param format json format
     * @return json object
     * @see JSONFormat
     */
    JsonObject toJson(JSONFormat format);

    /**
     * Covert to json object by Object mapper
     *
     * @param codec json codec
     * @return json object
     * @see JsonCodec
     */
    JsonObject toJson(JsonCodec codec);

    static <R extends Record> JsonRecord<R> create(@NotNull R record) {
        return new JsonRecordImpl<>(record);
    }

    static <R extends Record> JsonRecord<R> create(@NotNull TableLike<R> table) {
        return create(table.asTable().newRecord());
    }

}
