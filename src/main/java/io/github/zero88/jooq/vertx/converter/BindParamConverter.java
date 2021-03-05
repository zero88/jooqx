package io.github.zero88.jooq.vertx.converter;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.jooq.Converter;
import org.jooq.Param;

import io.github.zero88.jooq.vertx.converter.ext.PgConverter;
import io.vertx.core.buffer.Buffer;

import lombok.NonNull;

/**
 * Represents for a converter that transforms jOOQ Param to Vertx SQL bind value
 *
 * @param <T> Type of Vertx bind value holder
 * @see Param
 * @since 1.0.0
 */
public interface BindParamConverter<T> {

    T convert(@NonNull Map<String, Param<?>> params);

    List<T> convert(@NonNull Map<String, Param<?>> params, @NonNull BindBatchValues queryParams);

    @SuppressWarnings( {"unchecked", "rawtypes"})
    default Object convertToDatabaseType(String paramName, Param<?> param, BiFunction<String, Param<?>, ?> queryValue) {
        /*
         * https://github.com/reactiverse/reactive-pg-client/issues/191 enum types are treated as unknown
         * DataTypes. Workaround is to convert them to string before adding to the Tuple.
         */
        final Object val = queryValue.apply(paramName, param);
        if (Enum.class.isAssignableFrom(param.getBinding().converter().toType())) {
            if (val == null) {
                return null;
            }
            return val.toString();
        }
        // jooq treats BINARY types as byte[] but the reactive client expects a Buffer to write to blobs
        if (byte[].class.isAssignableFrom(param.getBinding().converter().fromType())) {
            byte[] bytes = ((Converter<byte[], Object>) param.getBinding().converter()).to(val);
            if (bytes == null) {
                return null;
            }
            return Buffer.buffer(bytes);
        }
        if (param.getBinding().converter() instanceof PgConverter) {
            return ((PgConverter) param.getBinding().converter()).rowConverter().to(val);
        }
        return ((Converter<Object, Object>) param.getBinding().converter()).to(val);
    }

}
