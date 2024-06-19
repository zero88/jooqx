package io.github.zero88.jooqx.datatype;

/**
 * The converter bridges the {@code org.jooq.Converter} used in {@code jooq < 3.18}, which enables {@code jooqx} to
 * support multiple jOOQ versions.
 *
 * @since 2.0.0
 */
public interface BridgeConverter<D, U> extends org.jooq.Converter<D, U> { }
