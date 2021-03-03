package io.github.zero88.jooq.vertx.converter;

import java.util.Collection;

import org.jooq.Param;

import io.github.zero88.jooq.vertx.converter.ext.PgConverter;
import io.vertx.core.buffer.Buffer;

/**
 * Represents for a converter that transforms jOOQ Param to Vertx SQL bind value
 *
 * @param <T> Type of Vertx bind value holder
 * @see Param
 * @since 1.0.0
 */
public interface ParamConverter<T> {

    T convert(Collection<Param<?>> params);

    default <U> Object convertToDatabaseType(Param<U> param) {
        /*
         * https://github.com/reactiverse/reactive-pg-client/issues/191 enum types are treated as unknown
         * DataTypes. Workaround is to convert them to string before adding to the Tuple.
         */
        if (Enum.class.isAssignableFrom(param.getBinding().converter().toType())) {
            if (param.getValue() == null) {
                return null;
            }
            return param.getValue().toString();
        }
        // jooq treats BINARY types as byte[] but the reactive client expects a Buffer to write to blobs
        if (byte[].class.isAssignableFrom(param.getBinding().converter().fromType())) {
            byte[] bytes = (byte[]) param.getBinding().converter().to(param.getValue());
            if (bytes == null) {
                return null;
            }
            return Buffer.buffer(bytes);
        }
        if (param.getBinding().converter() instanceof PgConverter) {
            return ((PgConverter) param.getBinding().converter()).rowConverter().to(param.getValue());
        }
        return param.getBinding().converter().to(param.getValue());
    }

}
