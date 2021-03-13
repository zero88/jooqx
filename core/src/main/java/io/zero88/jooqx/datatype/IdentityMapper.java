package io.zero88.jooqx.datatype;

import org.jooq.Converter;
import org.jooq.impl.SQLDataType;

import lombok.NonNull;

/**
 * It is short form of {@link DataTypeMapper} that means {@code jOOQ} record is used an user type that is same with
 * {@code Vert.x SQL data type}
 *
 * @param <V> Vert.x SQL data type and an user type
 * @param <T> The database type - i.e. any type available from {@link SQLDataType}
 * @see DataTypeMapper
 * @see Converter
 */
public interface IdentityMapper<V, T> extends DataTypeMapper<V, T, V> {

    @Override
    @SuppressWarnings("unchecked")
    default @NonNull Converter<V, T> jooqxConverter() {
        return (Converter<V, T>) this;
    }

}
