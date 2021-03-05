package io.github.zero88.jooq.vertx.converter;

import java.util.Map;
import java.util.function.BiFunction;

import org.jooq.Param;

import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.ArrayTuple;

public class ReactiveBindParamConverter extends AbstractBindParamConverter<Tuple> {

    protected ArrayTuple doConvert(Map<String, Param<?>> params, BiFunction<String, Param<?>, ?> queryValue) {
        final ArrayTuple bindValues = new ArrayTuple(params.size());
        params.entrySet()
              .stream()
              .filter(entry -> !entry.getValue().isInline())
              .forEachOrdered(et -> bindValues.addValue(convertToDatabaseType(et.getKey(), et.getValue(), queryValue)));
        return bindValues;
    }

}
