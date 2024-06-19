package io.github.zero88.jooqx.datatype;

import static io.github.zero88.jooqx.Utils.classRepr;
import static io.github.zero88.jooqx.Utils.parentheses;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import org.jooq.impl.SQLDataType;

/**
 * A <code>Mapper</code> for data types.
 * <p>
 * A general data type conversion interface that can be provided to {@code jOOQ.x} at various places in order to perform
 * custom data type conversion.
 * <p>
 * A mapper is three-way-conversion, this means that the mapper is used among {@code Vert.x SQL datatype} (on a
 * specified {@code Vert.x SQL client}), {@code jOOQ} {@link SQLDataType} and user data type
 *
 * @param <V> The Vert.x SQL data type
 * @param <J> The jOOQ type - i.e. any type available from {@link SQLDataType}
 * @param <U> The user type
 * @see Converter
 * @see UserTypeAsJooqType
 * @see UserTypeAsVertxType
 * @since 1.0.0
 */
public interface DataTypeMapper<V, J, U> extends BridgeConverter<J, U>, ConverterRepr<J, U> {

    /**
     * Defines jooqx converter between {@code Vert.x} data type as database data type and {@code jOOQ} data type as
     * intermediate type is used in record
     *
     * @return jooqx converter
     * @see JooqxConverter
     */
    JooqxConverter<V, J> jooqxConverter();

    /**
     * Convert from database object to user type object
     *
     * @param databaseObject the database object (Vert.x data type)
     * @return the user object
     */
    default U fromVtoU(V databaseObject) { return from(jooqxConverter().from(databaseObject)); }

    /**
     * Convert from user object to database object
     *
     * @param userObject the user object
     * @return the database object (Vert.x data type)
     */
    default V toVFromU(U userObject) { return jooqxConverter().to(to(userObject)); }

    @Override
    default String repr() {
        return classRepr(getClass()) + parentheses("->", fromType(), jooqxConverter().fromType(), toType());
    }

}
