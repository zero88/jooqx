package io.zero88.jooqx.datatype.basic;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UDTParserTest {

    @Test
    void test_null_or_empty() {
        Assertions.assertNull(UDTParser.parse(""));
        Assertions.assertNull(UDTParser.parse(null));
    }

    @Test
    void test_invalid_with_strict() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> UDTParser.parse("abc"));
    }

    @Test
    void test_invalid_with_non_strict() {
        final String[] udt = UDTParser.parse("abc", false);
        Assertions.assertNotNull(udt);
        Assertions.assertEquals(1, udt.length);
        Assertions.assertEquals("abc", udt[0]);
    }

    @Test
    void test_without_quote() {
        final String[] udt = UDTParser.parse("(abc,123,xyz)");
        System.out.println(Arrays.toString(udt));
        Assertions.assertEquals(3, udt.length);
        Assertions.assertEquals("abc", udt[0]);
        Assertions.assertEquals("123", udt[1]);
        Assertions.assertEquals("xyz", udt[2]);
    }

    @Test
    void test_null_item_in_first() {
        final String[] udt = UDTParser.parse("(,123,xyz)");
        System.out.println(Arrays.toString(udt));
        Assertions.assertEquals(3, udt.length);
        Assertions.assertEquals("", udt[0]);
        Assertions.assertEquals("123", udt[1]);
        Assertions.assertEquals("xyz", udt[2]);
    }

    @Test
    void test_null_item_in_last() {
        final String[] udt = UDTParser.parse("(abc,123,)");
        System.out.println(Arrays.toString(udt));
        Assertions.assertEquals(3, udt.length);
        Assertions.assertEquals("abc", udt[0]);
        Assertions.assertEquals("123", udt[1]);
        Assertions.assertEquals("", udt[2]);
    }

    @Test
    void test_null_item_between() {
        final String[] udt = UDTParser.parse("(abc,,xyz)");
        System.out.println(Arrays.toString(udt));
        Assertions.assertEquals(3, udt.length);
        Assertions.assertEquals("abc", udt[0]);
        Assertions.assertEquals("", udt[1]);
        Assertions.assertEquals("xyz", udt[2]);
    }

    @Test
    void test_quote_first() {
        final String[] udt = UDTParser.parse("(\"s t h\",abc,t)");
        System.out.println(Arrays.toString(udt));
        Assertions.assertEquals(3, udt.length);
        Assertions.assertEquals("s t h", udt[0]);
        Assertions.assertEquals("abc", udt[1]);
        Assertions.assertEquals("t", udt[2]);
    }

    @Test
    void test_quote_between() {
        final String[] udt = UDTParser.parse("(Anytown,\"Main St\",t)");
        System.out.println(Arrays.toString(udt));
        Assertions.assertEquals(3, udt.length);
        Assertions.assertEquals("Anytown", udt[0]);
        Assertions.assertEquals("Main St", udt[1]);
        Assertions.assertEquals("t", udt[2]);
    }

    @Test
    void test_quote_last() {
        final String[] udt = UDTParser.parse("(abc,123,\"x y z\")");
        System.out.println(Arrays.toString(udt));
        Assertions.assertEquals(3, udt.length);
        Assertions.assertEquals("abc", udt[0]);
        Assertions.assertEquals("123", udt[1]);
        Assertions.assertEquals("x y z", udt[2]);
    }

    @Test
    void test_comma_in_quote() {
        final String[] udt = UDTParser.parse("(\"Any,town\",xyz,f)");
        System.out.println(Arrays.toString(udt));
        Assertions.assertEquals(3, udt.length);
        Assertions.assertEquals("Any,town", udt[0]);
        Assertions.assertEquals("xyz", udt[1]);
        Assertions.assertEquals("f", udt[2]);
    }

    @Test
    void test_has_escaped_quote() {
        final String[] udt = UDTParser.parse("(Anytown,\"Main \"\" St\",t)");
        System.out.println(Arrays.toString(udt));
        Assertions.assertEquals(3, udt.length);
        Assertions.assertEquals("Anytown", udt[0]);
        Assertions.assertEquals("Main \" St", udt[1]);
        Assertions.assertEquals("t", udt[2]);
    }

}
