package io.github.zero88.jooqx.datatype;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Param;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A registry for list of specific data type mapper among {@code Vert.x}, {@code jOOQ} and {@code User data type}.
 *
 * @see DataTypeMapper
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public final class DataTypeMapperRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTypeMapperRegistry.class);
    private final Map<Class, DataTypeMapper> typeMappers = new HashMap<>();
    private final Map<Field, FieldMapper> fieldMappers = new HashMap<>();

    public <V, T, U> DataTypeMapperRegistry add(@NotNull DataTypeMapper<V, T, U> mapper) {
        LOGGER.debug("Adding global mapper by [{}]...", mapper.jooqxConverter().toType().getName());
        this.typeMappers.put(mapper.jooqxConverter().toType(), mapper);
        return this;
    }

    public <V, T, U> DataTypeMapperRegistry addByColumn(@NotNull Field column,
        @NotNull DataTypeMapper<V, T, U> mapper) {
        LOGGER.debug("Adding field mapper by [{}::{}]...", unquoted(column),
                     mapper.jooqxConverter().toType().getName());
        this.fieldMappers.put(column, new FieldMapper(column, mapper));
        return this;
    }

    public Object toDatabaseType(String paramName, Param<?> param, BiFunction<String, Param<?>, ?> queryValue) {
        final Object val = queryValue.apply(paramName, param);
        final Field<?> field = DSL.field(paramName, param.getDataType());
        final DataTypeMapper<Object, ?, Object> mapper = getTypeMapper("Param", field, val);
        if (Objects.nonNull(mapper)) {
            return mapper.toVFromU(val);
        }
        final Converter<?, ?> converter = param.getConverter();
        if (converter instanceof DataTypeMapper) {
            return ((DataTypeMapper) converter).toVFromU(val);
        }
        return ((Converter<Object, Object>) converter).to(val);
    }

    public Object toUserType(@NotNull Field<?> f, Object value) {
        final DataTypeMapper<Object, ?, Object> mapper = getTypeMapper("Field", f, value);
        if (Objects.nonNull(mapper)) {
            if (value == null || mapper.jooqxConverter().fromType().isInstance(value)) {
                return mapper.fromVtoU(value);
            }
            LOGGER.debug("Value of [{}::{}] is not mapped with registered type [{}], coerce value by [{}]...",
                         unquoted(f), value.getClass().getName(), mapper.jooqxConverter().fromType(),
                         mapper.jooqxConverter().toType());
            return ((Converter<Object, Object>) f.coerce(mapper.jooqxConverter().toType()).getConverter()).from(value);
        }
        final Converter<?, ?> converter = f.getConverter();
        if (converter instanceof DataTypeMapper) {
            return ((DataTypeMapper) converter).fromVtoU(value);
        }
        return ((Converter<Object, Object>) converter).from(value);
    }

    <V, T, U> DataTypeMapper<V, T, U> lookup(Field<?> field, Class<?> type) {
        LOGGER.debug("Lookup mapper by field and data type [{}::{}]...", unquoted(field), type.getName());
        final FieldMapper fieldMapper = fieldMappers.get(field);
        DataTypeMapper<V, T, U> mapper = Optional.ofNullable(fieldMapper)
                                                 .filter(pm -> pm.matchesByType(type))
                                                 .map(pm -> pm.mapper)
                                                 .orElseGet(() -> fieldMappers.values()
                                                                              .parallelStream()
                                                                              .filter(pm -> pm.matches(field, type))
                                                                              .map(pm -> pm.mapper)
                                                                              .findFirst()
                                                                              .orElse(typeMappers.get(type)));
        if (mapper == null) {
            LOGGER.debug("Use default converter for [{}::{}]", unquoted(field), field.getType().getName());
        } else {
            LOGGER.debug("Found mapper [{}::{}::{}::{}]", unquoted(field), mapper.jooqxConverter().fromType().getName(),
                         field.getType().getName(), mapper.toType());
        }
        return mapper;
    }

    private DataTypeMapper<Object, ?, Object> getTypeMapper(String which, Field<?> field, Object value) {
        LOGGER.debug("Converting {} [{}] - jOOQ Type [{}] - Vertx SQL Type [{}]...", which, unquoted(field),
                     field.getType().getName(),
                     Optional.ofNullable(value).map(Object::getClass).map(Class::getName).orElse(null));
        return this.lookup(field, field.getType());
    }

    private Name unquoted(Field column) {
        return DSL.unquotedName(column.getQualifiedName().getName());
    }

    private static class FieldMapper {

        @NotNull
        private final Field field;
        @NotNull
        private final DataTypeMapper mapper;

        public FieldMapper(@NotNull Field field, @NotNull DataTypeMapper mapper) {
            this.field  = field;
            this.mapper = mapper;
        }

        private boolean matchesByType(@NotNull Class jooqDataTypeClazz) {
            return mapper.jooqxConverter().toType().equals(jooqDataTypeClazz);
        }

        private boolean matches(@NotNull Field column, @NotNull Class jooqDataTypeClazz) {
            return field.equals(column) && matchesByType(jooqDataTypeClazz);
        }

    }

}
