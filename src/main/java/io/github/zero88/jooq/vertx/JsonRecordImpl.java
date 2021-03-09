package io.github.zero88.jooq.vertx;

import java.util.Arrays;

import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.impl.CustomRecord;

import io.vertx.core.json.JsonObject;

final class JsonRecordImpl<R extends TableRecord<R>> extends CustomRecord<R> implements JsonRecord<R> {

    public JsonRecordImpl(Table<R> table) {
        super(table);
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        Arrays.stream(this.fields()).forEach(f -> json.put(f.getName(), f.get(this)));
        return json;
    }

}
