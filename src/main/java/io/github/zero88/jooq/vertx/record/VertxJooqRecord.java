package io.github.zero88.jooq.vertx.record;

import java.util.Arrays;

import org.jooq.Record;
import org.jooq.Table;
import org.jooq.TableRecord;
import org.jooq.impl.CustomRecord;

import io.vertx.core.json.JsonObject;

import lombok.NonNull;

public class VertxJooqRecord<R extends TableRecord<R>> extends CustomRecord<R> {

    public VertxJooqRecord(Table<R> table) {
        super(table);
    }

    public JsonObject toJson() {
        final JsonObject json = new JsonObject();
        Arrays.stream(this.fields()).forEach(f -> json.put(f.getName(), f.get(this)));
        return json;
    }

    public <REC extends Record> REC into(@NonNull REC record) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
