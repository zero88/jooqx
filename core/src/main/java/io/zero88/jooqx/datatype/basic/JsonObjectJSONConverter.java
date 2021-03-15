package io.zero88.jooqx.datatype.basic;

import org.jetbrains.annotations.NotNull;
import org.jooq.JSON;

import io.vertx.core.json.JsonObject;
import io.zero88.jooqx.datatype.JooqxConverter;

public final class JsonObjectJSONConverter implements JooqxConverter<JsonObject, JSON> {

    @Override
    public JSON from(JsonObject vertxObject) { return vertxObject == null ? null : JSON.valueOf(vertxObject.encode()); }

    @Override
    public JsonObject to(JSON jooqObject) { return jooqObject == null ? null : new JsonObject(jooqObject.data()); }

    @Override
    public @NotNull Class<JsonObject> fromType() { return JsonObject.class; }

    @Override
    public @NotNull Class<JSON> toType() { return JSON.class; }

}
