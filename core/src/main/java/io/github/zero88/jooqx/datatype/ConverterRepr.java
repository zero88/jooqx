package io.github.zero88.jooqx.datatype;

import static io.github.zero88.jooqx.Utils.classRepr;
import static io.github.zero88.jooqx.Utils.parentheses;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jooq.Converter;

/**
 * Converter representation that use for {@code toString()} in logging
 *
 * @since 2.0.0
 */
@Internal
public interface ConverterRepr<J, U> extends BridgeConverter<J, U> {

    default String repr() {
        return ConverterRepr.repr(this);
    }

    static String repr(Converter<?, ?> converter) {
        return classRepr(converter.getClass()) + parentheses("->", converter.fromType(), converter.toType());
    }

}
