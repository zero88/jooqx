package io.github.zero88.jooqx;

import java.util.Map;

import org.jooq.JSONFormat;
import org.jooq.Record;

import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.json.JsonCodec;

final class JsonRecordImpl<R extends Record> implements JsonRecord<R> {

    final R record;
    JsonObject json;

    JsonRecordImpl(R record) { this.record = record; }

    @Override
    public R record() {
        return record;
    }

    public JsonObject toJson() {
        if (json == null) {
            return json = new JsonObject(record.intoMap());
        }
        return json;
    }

    @Override
    public JsonObject toJson(JSONFormat format) {
        return new JsonObject(record.formatJSON(format));
    }

    @Override
    @SuppressWarnings("unchecked")
    public JsonObject toJson(JsonCodec codec) {
        return codec == null
               ? toJson()
               : new JsonObject((Map<String, Object>) codec.fromValue(record.intoMap(), Map.class));
    }

    @Override
    public String toString() {
        return record.toString();
    }

}
