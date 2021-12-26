package io.zero88.rsql;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LikeWildcardPatternTest {

    @Test
    void test_convert() {
        final String argument = "j*va j*q r?l zero88 5\\* 100%";
        final String parameter = "j%va j%q r_l zero88 5\\* 100\\%";
        Assertions.assertEquals(parameter, LikeWildcardPattern.DEFAULT.convert(argument));
    }

    @Test
    void test_is_regex() {
        final String argument = "java{1,3}";
        final String parameter = "java{1,3}";
        Assertions.assertEquals(parameter, ((LikeWildcardPattern) () -> true).convert(argument));
    }
}