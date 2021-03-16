package io.zero88.jooqx.datatype.basic;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.jooq.JSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.data.Numeric;

class BasicConverterTest {

    @Test
    void test_byte_buffer() {
        final BytesConverter bytesConverter = new BytesConverter();
        final Buffer buffer = Buffer.buffer("jooqx");
        byte[] bytes = "jooqx".getBytes(StandardCharsets.UTF_8);
        Assertions.assertTrue(Arrays.equals(bytes, bytesConverter.from(buffer)));
        Assertions.assertEquals(buffer, bytesConverter.to(bytes));
    }

    @Test
    void test_big_decimal() {
        final BigDecimalConverter converter = new BigDecimalConverter();
        final Numeric numeric = Numeric.create(10.987654321);
        final BigDecimal bigDecimal = BigDecimal.valueOf(10.987654321);
        Assertions.assertEquals(bigDecimal, converter.from(numeric));
        Assertions.assertEquals(numeric, converter.to(bigDecimal));
    }

    @Test
    void test_json() {
        final JsonObjectJSONConverter converter = new JsonObjectJSONConverter();
        JsonObject jsonObject = new JsonObject().put("hello", "jooqx");
        JSON json = JSON.json("{\"hello\":\"jooqx\"}");
        Assertions.assertEquals(json, converter.from(jsonObject));
        Assertions.assertEquals(jsonObject, converter.to(json));
    }

}
