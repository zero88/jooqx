package io.github.zero88.jooq.vertx;

import java.util.Arrays;

import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.impl.CustomRecord;

import io.vertx.core.json.JsonObject;

final class VertxJooqRecordImpl<R extends TableRecord<R>> extends CustomRecord<R> implements VertxJooqRecord<R> {

    public VertxJooqRecordImpl(Table<R> table) {
        super(table);
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        Arrays.stream(this.fields()).forEach(f -> json.put(f.getName(), f.get(this)));
        return json;
    }

}
