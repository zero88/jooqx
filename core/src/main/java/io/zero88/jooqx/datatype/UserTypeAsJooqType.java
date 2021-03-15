package io.zero88.jooqx.datatype;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import org.jooq.impl.SQLDataType;

/**
 * It is short form of {@link DataTypeMapper} that means a {@code record field} is used user type as {@code jOOQ} data
 * type
 *
 * @param <V> Vert.x SQL data type
 * @param <J> The jOOQ data type and an user type - i.e. any type available from {@link SQLDataType}
 * @apiNote In nutshell, it is equals with default {@link JooqxConverter}
 * @see DataTypeMapper
 * @see Converter
 */
public interface UserTypeAsJooqType<V, J> extends DataTypeMapper<V, J, J> {

    @Override
    default J from(J databaseObject) {
        return databaseObject;
    }

    @Override
    default J to(J userObject) {
        return userObject;
    }

    @Override
    default @NotNull Class<J> fromType() {
        return jooqxConverter().toType();
    }

    @Override
    default @NotNull Class<J> toType() {
        return jooqxConverter().toType();
    }

    /**
     * Create new instance UserTypeAsJooqType by {@link JooqxConverter}
     *
     * @param jooqxConverter jooqx converter
     * @param <V>            The Vert.x SQL data type and an user type
     * @param <J>            The jOOQ type
     * @return new instance
     */
    static <V, J> UserTypeAsJooqType<V, J> create(JooqxConverter<V, J> jooqxConverter) {
        return () -> jooqxConverter;
    }

}
