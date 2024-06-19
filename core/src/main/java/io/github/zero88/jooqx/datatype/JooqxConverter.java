package io.github.zero88.jooqx.datatype;

import org.jetbrains.annotations.NotNull;
import org.jooq.Converter;
import org.jooq.impl.SQLDataType;

/**
 * Defines basic datatype converter between {@code Vert.x datatype} as database data type and {@code jOOQ SQL data type}
 * as an intermediate type is used in jOOQ record
 * <p>
 * In the specified database, depends on {@code Vertx. SQL client}, we will have a list of particular {@code jOOQ.x
 * converter}. See {@code spi} modules depends on database for more information
 *
 * @param <V> The Vertx data type as database data type
 * @param <J> The jOOQ data type as an intermediate type is used in jOOQ record
 * @see SQLDataType
 * @see Converter
 */
public interface JooqxConverter<V, J> extends BridgeConverter<V, J> {

    /**
     * Convert the Vert.x object to jOOQ object
     *
     * @param vertxObject the vert.x object
     * @return the user object
     */
    @Override
    J from(V vertxObject);

    /**
     * Convert the jOOQ object to Vert.x object
     *
     * @param jooqObject the jOOQ object
     * @return the Vert.x object
     */
    @Override
    V to(J jooqObject);

    /**
     * @return the Vert.x data type
     */
    @Override
    @NotNull Class<V> fromType();

    /**
     * @return the jOOQ data type
     */
    @Override
    @NotNull Class<J> toType();

    @Override
    @NotNull
    default JooqxConverter<J, V> inverse() {
        return new JooqxConverter<J, V>() {
            @Override
            public V from(J vertxObject) { return JooqxConverter.this.to(vertxObject); }

            @Override
            public J to(V jooqObject) { return JooqxConverter.this.from(jooqObject); }

            @Override
            public @NotNull Class<J> fromType() { return JooqxConverter.this.toType(); }

            @Override
            public @NotNull Class<V> toType() { return JooqxConverter.this.fromType(); }
        };
    }

}
