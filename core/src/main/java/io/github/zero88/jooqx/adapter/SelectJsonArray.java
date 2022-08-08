package io.github.zero88.jooqx.adapter;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jooq.Record;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public final class SelectJsonArray extends SQLResultAdapterImpl<JsonObject, JsonArray> {

    public SelectJsonArray(@NotNull RecordFactory<? extends Record, JsonObject> recordFactory) {
        super(recordFactory);
    }

    @Override
    public @NotNull SelectStrategy strategy() {
        return SelectStrategy.MANY;
    }

    @Override
    public JsonArray collect(@NotNull List<JsonObject> jsonObjects) {
        return new JsonArray(jsonObjects);
    }

}
