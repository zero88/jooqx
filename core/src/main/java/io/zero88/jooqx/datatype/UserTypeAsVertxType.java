package io.zero88.jooqx.datatype;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import org.jooq.impl.SQLDataType;

/**
 * It is short form of {@link DataTypeMapper} that means  a {@code record field} is used a user type that is same with
 * {@code Vert.x SQL data type}
 *
 * @param <V> The Vert.x SQL data type and a user type
 * @param <J> The jOOQ type - i.e. any type available from {@link SQLDataType}
 * @apiNote It is inverse of default {@link JooqxConverter} and required your application generate custom data types
 *     as Vert.x type. See <a href="https://www.jooq.org/doc/3.2/manual/code-generation/custom-data-types/">jOOQ
 *     generation</a>
 * @see DataTypeMapper
 * @see Converter
 */
public interface UserTypeAsVertxType<V, J> extends DataTypeMapper<V, J, V>, JooqxConverter<J, V> {

    @Override
    default JooqxConverter<V, J> jooqxConverter() {
        return this.inverse();
    }

    /**
     * Create new instance UserTypeAsVertxType by {@link JooqxConverter}
     *
     * @param jooqxConverter jooqx converter
     * @param <V>            The Vert.x SQL data type and a user type
     * @param <J>            The jOOQ type
     * @return new instance
     */
    static <V, J> UserTypeAsVertxType<V, J> create(@NotNull JooqxConverter<V, J> jooqxConverter) {
        return new UserTypeAsVertxType<V, J>() {
            @Override
            public V from(J jooqObject) { return jooqxConverter.to(jooqObject); }

            @Override
            public J to(V vertxObject) { return jooqxConverter.from(vertxObject); }

            @Override
            public @NotNull Class<J> fromType() { return jooqxConverter.toType(); }

            @Override
            public @NotNull Class<V> toType() { return jooqxConverter.fromType(); }
        };
    }

}
