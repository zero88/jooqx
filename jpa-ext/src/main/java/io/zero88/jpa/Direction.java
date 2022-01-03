package io.zero88.jpa;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents for {@code Sort Direction}.
 *
 * @since 1.0.0
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Direction implements Serializable {
    /**
     * {@code ASC} direction.
     */
    ASC('+'),
    /**
     * {@code DESC} direction.
     */
    DESC('-');

    private final char symbol;

    /**
     * Parse direction.
     *
     * @param direction the direction
     * @return the direction
     * @since 1.0.0
     */
    public static Direction parse(String direction) {
        String d = Optional.ofNullable(direction).map(String::trim).orElse(null);
        if (Objects.isNull(d) || d.equals("")) {
            return ASC;
        }
        if (d.length() == 1) {
            return parse(d.charAt(0));
        }
        return Stream.of(Direction.values()).filter(t -> t.name().equalsIgnoreCase(d)).findFirst().orElse(ASC);
    }

    public static Direction parse(char direction) {
        return direction == DESC.getSymbol() ? DESC : ASC;
    }

    public boolean isASC() {
        return ASC == this;
    }

    public boolean isDESC() {
        return DESC == this;
    }
}
