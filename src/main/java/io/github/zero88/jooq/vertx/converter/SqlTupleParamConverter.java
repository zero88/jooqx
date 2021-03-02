package io.github.zero88.jooq.vertx.converter;

import java.util.Collection;

import org.jooq.Param;

import io.vertx.sqlclient.Tuple;
import io.vertx.sqlclient.impl.ArrayTuple;

public class SqlTupleParamConverter implements ParamConverter<Tuple> {

    @Override
    public Tuple convert(Collection<Param<?>> params) {
        ArrayTuple bindValues = new ArrayTuple(params.size());
        for (Param<?> param : params) {
            if (!param.isInline()) {
                bindValues.addValue(convertToDatabaseType(param));
            }
        }
        return bindValues;
    }

}
