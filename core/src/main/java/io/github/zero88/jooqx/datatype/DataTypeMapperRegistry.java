package io.github.zero88.jooqx.datatype;

import static io.github.zero88.jooqx.Utils.brackets;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Param;
import org.jooq.impl.DSL;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;

/**
 * A registry for list of specific data type mapper among {@code Vert.x}, {@code jOOQ} and {@code User data type}.
 *
 * @see DataTypeMapper
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public final class DataTypeMapperRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataTypeMapperRegistry.class);
    private final Map<Class, DataTypeMapper> typeMappers = new HashMap<>();
    private final Map<Field, FieldMapper> fieldMappers = new HashMap<>();

    public <V, T, U> DataTypeMapperRegistry add(@NotNull DataTypeMapper<V, T, U> mapper) {
        LOGGER.debug("Adding global mapper by " + brackets(mapper.repr()) + "...");
        this.typeMappers.put(mapper.jooqxConverter().toType(), mapper);
        return this;
    }

    public <V, T, U> DataTypeMapperRegistry addByColumn(@NotNull Field column,
                                                        @NotNull DataTypeMapper<V, T, U> mapper) {
        LOGGER.debug(
            "Adding field mapper by " + brackets(unquoted(column), column.getType()) + brackets(mapper.repr()) + "...");
        this.fieldMappers.put(column, new FieldMapper(column, mapper));
        return this;
    }

    public Object toDatabaseType(String paramName, Param<?> param, BiFunction<String, Param<?>, ?> queryValue) {
        final Object value = queryValue.apply(paramName, param);
        final Field<?> field = DSL.field(paramName, param.getDataType());
        LOGGER.trace("Converting Param" + brackets(unquoted(field), field.getType()) + " - Value Type" +
                     brackets(value == null ? null : value.getClass()) + "...");
        final Converter<?, ?> converter = lookup(field, field.getType());
        if (converter instanceof DataTypeMapper) {
            return ((DataTypeMapper) converter).toVFromU(value);
        }
        return ((Converter<Object, Object>) converter).to(value);
    }

    public Object toUserType(@NotNull Field<?> field, Object value) {
        LOGGER.trace("Converting Field" + brackets(unquoted(field), field.getType()) + " - Value Type" +
                     brackets(value == null ? null : value.getClass()) + "...");
        final Converter<?, ?> converter = lookup(field, field.getType());
        if (converter instanceof DataTypeMapper) {
            final DataTypeMapper mapper = (DataTypeMapper) converter;
            final JooqxConverter<?, ?> jc = mapper.jooqxConverter();
            if (value == null || jc.fromType().isInstance(value)) {
                return mapper.fromVtoU(value);
            }
            LOGGER.debug("Value of " + brackets(unquoted(field), field.getType(), value.getClass()) +
                         " is not mapped with registered type " + brackets(jc.fromType()) + ", coerce value by " +
                         brackets(jc.toType()) + "...");
            return ((Converter<Object, Object>) field.coerce(jc.toType()).getConverter()).from(value);
        }
        return ((Converter<Object, Object>) converter).from(value);
    }

    Converter<?, ?> lookup(Field<?> field, Class<?> type) {
        LOGGER.trace("Lookup mapper by field and data type " + brackets(unquoted(field), type) + "...");
        final FieldMapper fieldMapper = fieldMappers.get(field);
        DataTypeMapper mapper = Optional.ofNullable(fieldMapper)
                                        .filter(pm -> pm.matchesByType(type))
                                        .map(pm -> pm.mapper)
                                        .orElseGet(() -> fieldMappers.values()
                                                                     .parallelStream()
                                                                     .filter(pm -> pm.matches(field, type))
                                                                     .map(pm -> pm.mapper)
                                                                     .findFirst()
                                                                     .orElse(typeMappers.get(type)));
        if (mapper == null) {
            LOGGER.debug("Use default converter" + brackets(unquoted(field), field.getType()) +
                         brackets(ConverterRepr.repr(field.getConverter())));
            return field.getConverter();
        }
        LOGGER.debug("Found mapper " + brackets(unquoted(field), field.getType()) + brackets(mapper.repr()));
        return mapper;
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
