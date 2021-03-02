package io.github.zero88.jooq.vertx.converter.ext;

import java.util.function.Function;

import org.jooq.Converter;
import org.jooq.JSON;

import io.vertx.core.json.JsonArray;

/**
 * @author jensklingsporn
 */
public class JSONToJsonArrayBinding extends PGJsonToVertxJsonBinding<JSON, JsonArray> {

    @Override
    public Converter<JSON, JsonArray> converter() {
        return JSONToJsonArrayConverter.getInstance();
    }

    @Override
    Function<String, JSON> valueOf() {
        return JSON::valueOf;
    }

    @Override
    String coerce() {
        return "::json";
    }

}
