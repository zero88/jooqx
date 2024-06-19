package io.github.zero88.jooqx.datatype;

import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * The converter bridges the {@code org.jooq.ContextConverter} is introduced since {@code jooq >= 3.18}, which enables
 * {@code jooqx} to support multiple jOOQ versions.
 *
 * @since 2.0.0
 */
@Internal
public interface BridgeConverter<D, U> extends org.jooq.ContextConverter<D, U> {

    default U from(D databaseObject, org.jooq.ConverterContext ctx) {
        return from(databaseObject);
    }

    default D to(U userObject, org.jooq.ConverterContext ctx) {
        return to(userObject);
    }

    @Override
    D to(U userObject);

    @Override
    U from(D databaseObject);

}
