package io.zero88.jpa;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import io.zero88.jpa.Order.OrderSerializer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Represents for Order.
 *
 * @since 1.0.0
 */
@JsonSerialize(using = OrderSerializer.class)
public final class Order implements Serializable {

    private final String property;
    private final Direction direction;

    private Order(@NotNull String property, @NotNull Direction direction) {
        this.property  = property;
        this.direction = direction;
    }

    /**
     * By asc order.
     *
     * @param property the property
     * @return the order
     * @since 1.0.0
     */
    public static Order byASC(@NotNull String property) {
        return by(property, Direction.ASC);
    }

    /**
     * By desc order.
     *
     * @param property the property
     * @return the order
     * @since 1.0.0
     */
    public static Order byDESC(@NotNull String property) {
        return by(property, Direction.DESC);
    }

    /**
     * By order.
     *
     * @param property  the property
     * @param direction the direction
     * @return the order
     * @since 1.0.0
     */
    public static Order by(@NotNull String property, Direction direction) {
        return new Order(property, Optional.ofNullable(direction).orElse(Direction.ASC));
    }

    /**
     * By order.
     *
     * @param property  the property
     * @param direction the direction
     * @return the order
     * @since 1.0.0
     */
    public static Order by(@NotNull @JsonProperty("property") String property,
        @JsonProperty("direction") String direction) {
        return by(property, Direction.parse(direction));
    }

    @JsonCreator
    private static Order by(@NotNull Map<String, String> properties) {
        final Entry<String, String> val = properties.entrySet()
                                                    .stream()
                                                    .findFirst()
                                                    .orElseThrow(() -> new RuntimeException("Empty value"));
        return by(val.getKey(), Direction.parse(val.getValue()));
    }

    public @NotNull String property()     {return this.property;}

    public @NotNull Direction direction() {return this.direction;}

    public String toString() {
        return this.direction.getSymbol() + this.property;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Order order = (Order) o;

        if (!property.equals(order.property)) {
            return false;
        }
        return direction == order.direction;
    }

    @Override
    public int hashCode() {
        int result = property.hashCode();
        result = 31 * result + direction.hashCode();
        return result;
    }

    public static class OrderSerializer extends JsonSerializer<Order> {

        @Override
        public void serialize(Order value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeObjectField(value.property(), value.direction());
            gen.writeEndObject();
        }

    }

}
