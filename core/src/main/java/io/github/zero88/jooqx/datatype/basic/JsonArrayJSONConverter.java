package io.github.zero88.jooqx.datatype.basic;

import org.jetbrains.annotations.NotNull;
import org.jooq.JSON;

import io.github.zero88.jooqx.datatype.JooqxConverter;
import io.vertx.core.json.JsonArray;

public final class JsonArrayJSONConverter implements JooqxConverter<JsonArray, JSON> {

    @Override
    public JSON from(JsonArray vertxObject) { return vertxObject == null ? null : JSON.valueOf(vertxObject.encode()); }

    @Override
    public JsonArray to(JSON jooqObject) { return jooqObject == null ? null : new JsonArray(jooqObject.data()); }

    @Override
    public @NotNull Class<JsonArray> fromType() { return JsonArray.class; }

    @Override
    public @NotNull Class<JSON> toType() { return JSON.class; }

}
