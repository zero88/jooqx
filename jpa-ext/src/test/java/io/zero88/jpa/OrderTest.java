package io.zero88.jpa;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderTest {

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void test_serialize_and_deserialize_ASC_order() throws IOException {
        final String orderJson = objectMapper.writeValueAsString(Order.byASC("xyz"));
        Assertions.assertEquals("{\"xyz\":\"ASC\"}", orderJson);

        final Order order = objectMapper.readValue(orderJson, Order.class);
        Assertions.assertTrue(order.direction().isASC());
        Assertions.assertFalse(order.direction().isDESC());
        Assertions.assertEquals("xyz", order.property());
        Assertions.assertEquals("Order(property=xyz, direction=ASC)", order.toString());

        Assertions.assertEquals(orderJson, objectMapper.writeValueAsString(order));
        Assertions.assertEquals(Order.by("xyz", ""), order);
    }

    @Test
    public void test_serialize_and_deserialize_DESC_order() throws IOException {
        final String orderJson = objectMapper.writeValueAsString(Order.by("abc", "-"));
        Assertions.assertEquals("{\"abc\":\"DESC\"}", orderJson);

        final Order order = objectMapper.readValue(orderJson, Order.class);
        Assertions.assertFalse(order.direction().isASC());
        Assertions.assertTrue(order.direction().isDESC());
        Assertions.assertEquals("abc", order.property());
        Assertions.assertEquals("Order(property=abc, direction=DESC)", order.toString());

        Assertions.assertEquals(orderJson, objectMapper.writeValueAsString(order));
        Assertions.assertEquals(Order.byDESC("abc"), order);
    }

}
