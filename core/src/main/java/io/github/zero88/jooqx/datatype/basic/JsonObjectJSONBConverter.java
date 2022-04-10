package io.github.zero88.jooqx.datatype.basic;

import org.jetbrains.annotations.NotNull;
import org.jooq.JSONB;

import io.github.zero88.jooqx.datatype.JooqxConverter;
import io.vertx.core.json.JsonObject;

public final class JsonObjectJSONBConverter implements JooqxConverter<JsonObject, JSONB> {

    @Override
    public JSONB from(JsonObject vertxObject) {
        return vertxObject == null ? null : JSONB.valueOf(vertxObject.encode());
    }

    @Override
    public JsonObject to(JSONB jooqObject) { return jooqObject == null ? null : new JsonObject(jooqObject.data()); }

    @Override
    public @NotNull Class<JsonObject> fromType() { return JsonObject.class; }

    @Override
    public @NotNull Class<JSONB> toType() { return JSONB.class; }

}
