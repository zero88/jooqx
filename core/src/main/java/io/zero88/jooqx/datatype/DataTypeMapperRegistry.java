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

import lombok.NonNull;

/**
 * A registry for list of specific data type mapper
 *
 * @see DataTypeMapper
 */
@SuppressWarnings("unchecked")
public final class DataTypeMapperRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTypeMapperRegistry.class);
    private final Map<Class, DataTypeMapper> mappers = new HashMap<>();

    public <V, T, U> DataTypeMapperRegistry add(@NonNull DataTypeMapper<V, T, U> mapper) {
        LOGGER.debug("Adding mapper by data type [{}]...", mapper.jooqxConverter().toType());
        this.mappers.put(mapper.jooqxConverter().toType(), mapper);
        return this;
    }

    private <V, T, U> DataTypeMapper<V, T, U> lookup(String fieldName, Class<T> dataType) {
        LOGGER.debug("Lookup mapper by data type [{}]...", dataType.getName());
        return this.mappers.get(dataType);
    }

    public Object toDatabaseType(String paramName, Param<?> param, BiFunction<String, Param<?>, ?> queryValue) {
        final Object val = queryValue.apply(paramName, param);
        final DataTypeMapper<Object, ?, Object> mapper = getTypeMapper("Param", paramName, param.getType(), val);
        if (Objects.nonNull(mapper)) {
            return mapper.toVFromU(val);
        }
        return ((Converter<Object, Object>) param.getConverter()).to(val);
    }

    public Object toUserType(@NonNull Field<?> f, Object value) {
        final DataTypeMapper<Object, ?, Object> mapper = getTypeMapper("Field", f.getName(), f.getType(), value);
        if (Objects.nonNull(mapper)) {
            return mapper.fromVtoU(value);
        }
        return ((Converter<Object, Object>) f.getConverter()).to(value);
    }

    private DataTypeMapper<Object, ?, Object> getTypeMapper(String which, String fieldName, Class<?> classType,
                                                            Object value) {
        LOGGER.debug("Converting {} [{}] - jOOQ [{}] - Vertx [{}]...", which, fieldName, classType.getName(),
                     Optional.ofNullable(value).map(Object::getClass).map(Class::getName).orElse(null));
        final DataTypeMapper<Object, Object, Object> mapper = this.lookup(fieldName, (Class<Object>) classType);
        if (mapper == null) {
            LOGGER.debug("Use default converter for [{}]", classType.getName());
            return null;
        }
        LOGGER.debug("Found mapper [{}::{}::{}::{}]", fieldName, mapper.jooqxConverter().fromType().getName(),
                     classType.getName(), mapper.toType());
        return mapper;
    }

}
