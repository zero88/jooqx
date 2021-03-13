package io.zero88.jooqx.datatype;

import org.jooq.Converter;
import org.jooq.impl.SQLDataType;

import lombok.NonNull;

/**
 * A <code>Mapper</code> for data types.
 * <p>
 * A general data type conversion interface that can be provided to jOOQ.x at various places in order to perform custom
 * data type conversion.
 * <p>
 * A mapper is three-way-conversion, this means that the mapper is used among {@code Vert.x SQL datatype} on a specified
 * {@code Vert.x SQL client}, {@code jOOQ} {@link SQLDataType} and user data type
 *
 * @param <V> The Vert.x SQL data type
 * @param <T> The database type - i.e. any type available from {@link SQLDataType}
 * @param <U> The user type
 * @see Converter
 * @since 1.0.0
 */
public interface DataTypeMapper<V, T, U> extends Converter<T, U> {

    @NonNull Converter<V, T> jooqxConverter();

}
