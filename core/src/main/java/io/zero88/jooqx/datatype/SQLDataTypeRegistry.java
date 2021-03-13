package io.zero88.jooqx.datatype;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import org.jooq.Converter;
import org.jooq.Field;
import org.jooq.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.buffer.Buffer;

import lombok.NonNull;

/**
 * @see DataTypeMapper
 */
public final class SQLDataTypeRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLDataTypeRegistry.class);
    private final Map<Class, DataTypeMapper> mappers = new HashMap<>();

    public <V, T, U> SQLDataTypeRegistry add(@NonNull DataTypeMapper<V, T, U> mapper) {
        this.mappers.put(mapper.fromType(), mapper);
        return this;
    }

    public <V, T, U> DataTypeMapper<V, T, U> lookup(Class<T> dataType) {
        return this.mappers.get(dataType);
    }

    @SuppressWarnings( {"unchecked"})
    public final Object toDatabaseType(String paramName, Param<?> param, BiFunction<String, Param<?>, ?> queryValue) {
        /*
         * https://github.com/reactiverse/reactive-pg-client/issues/191 enum types are treated as unknown
         * DataTypes. Workaround is to convert them to string before adding to the Tuple.
         */
        final Object val = queryValue.apply(paramName, param);
        if (Enum.class.isAssignableFrom(param.getConverter().toType())) {
            if (val == null) {
                return null;
            }
            return val.toString();
        }
        // jooq treats BINARY types as byte[] but the reactive client expects a Buffer to write to blobs
        if (byte[].class.isAssignableFrom(param.getConverter().fromType())) {
            byte[] bytes = ((Converter<byte[], Object>) param.getConverter()).to(val);
            if (bytes == null) {
                return null;
            }
            return Buffer.buffer(bytes);
        }
        return ((Converter<Object, Object>) param.getConverter()).to(val);
    }

    public Object convertFieldType(@NonNull Field f, Object value) {
        LOGGER.debug("Convert Field [{}] - jOOQ [{}] - Vertx [{}::{}]", f.getName(), f.getType().getName(), value,
                     Optional.ofNullable(value).map(Object::getClass).map(Class::getName).orElse(null));
        if (Objects.isNull(value)) {
            return null;
        }
        //            LOGGER.error("DataType: [{}] - Converter: [{}] - After convert [{}]", f.getDataType(), f
        //            .getConverter(),
        //                         Optional.ofNullable(f.getConverter().to(value))
        //                                 .map(v -> v.getClass().getName())
        //                                 .orElse(null));
        //                if (f.getType() == YearToSecond.class) {
        //                    record.set((Field<Object>) f, null);
        //                    return;
        //                }
        return value;
    }

}
