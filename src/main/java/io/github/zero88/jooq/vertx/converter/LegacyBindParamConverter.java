package io.github.zero88.jooq.vertx.converter;

import java.util.Map;
import java.util.function.BiFunction;

import org.jooq.Param;

import io.vertx.core.json.JsonArray;

public final class LegacyBindParamConverter extends AbstractBindParamConverter<JsonArray> {

    @Override
    protected JsonArray doConvert(Map<String, Param<?>> params, BiFunction<String, Param<?>, ?> queryValue) {
        JsonArray array = new JsonArray();
        params.entrySet()
              .stream()
              .filter(entry -> !entry.getValue().isInline())
              .forEachOrdered(entry -> array.add(convertToDatabaseType(entry.getKey(), entry.getValue(), queryValue)));
        return array;
    }

}
