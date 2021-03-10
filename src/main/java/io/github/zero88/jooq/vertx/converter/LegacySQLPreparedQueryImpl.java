package io.github.zero88.jooq.vertx.converter;

import java.util.Map;
import java.util.function.BiFunction;

import org.jooq.Configuration;
import org.jooq.Param;
import org.jooq.Query;
import org.jooq.conf.ParamType;

import io.vertx.core.json.JsonArray;

import lombok.NonNull;

final class LegacySQLPreparedQueryImpl extends SQLPreparedQueryImpl<JsonArray> implements LegacySQLPreparedQuery {

    public String sql(@NonNull Configuration configuration, @NonNull Query query) {
        return sql(configuration, query, ParamType.INDEXED);
    }

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
