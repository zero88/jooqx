package io.github.zero88.jooqx;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import io.github.zero88.jooqx.JooqxVersion.IncompatibleVersion;
import io.vertx.core.json.JsonObject;

class JooqxVersionTest {

    @Test
    void test_version_should_be_available() {
        final String buildVersion = System.getProperty("jooqx-build-version");
        Assertions.assertNotNull(buildVersion);
        Assertions.assertEquals(buildVersion, JooqxVersion.getVersion());
    }

    @Test
    void test_version_json_should_be_available() {
        final String buildVersion = System.getProperty("jooqx-build-version");
        Assertions.assertNotNull(buildVersion);
        final JsonObject jsonObject = JooqxVersion.versionComponents();
        Assertions.assertEquals(buildVersion, jsonObject.getString("jooq.x"));
        Assertions.assertTrue(jsonObject.containsKey("jOOQ"));
        Assertions.assertTrue(jsonObject.containsKey("Vert.x"));
    }

    @ParameterizedTest
    //@formatter:off
    @CsvSource({
        // jooq.x `+jvm8`
        "2.0.0+jvm8-SNAPSHOT,3.14.9,true", "2.0.0+jvm8-SNAPSHOT,3.17.26,true", "2.0.0+jvm8-SNAPSHOT,3.18.5,false",
        "2.0.0+jvm8,3.14.9,true", "2.0.0+jvm8,3.17.26,true", "2.0.0+jvm8,3.18.5,false",
        "2.0.0-rc3+jvm8,3.14.9,true", "2.0.0-rc3+jvm8,3.17.26,true", "2.0.0-rc3+jvm8,3.18.5,false",
        "2.0.0+hf1+jvm8,3.14.9,true", "2.0.0+hf1+jvm8,3.17.26,true", "2.0.0+hf1+jvm8,3.18.5,false",
        // jooq.x
        "2.0.0-SNAPSHOT,3.18.5,true", "2.0.0-SNAPSHOT,3.19.6,true", "2.0.0-SNAPSHOT,3.17.26,false",
        "2.0.0,3.18.5,true", "2.0.0,3.19.6,true", "2.0.0,3.17.26,false",
        "2.0.0-rc3,3.18.5,true", "2.0.0-rc3,3.19.6,true", "2.0.0-rc3,3.17.26,false",
        "2.0.0+hf1,3.18.5,true", "2.0.0+hf1,3.19.6,true", "2.0.0+hf1,3.17.26,false",
    })
    //@formatter:on
    void test_validate_jooq_compatibility_version(String jooqxVersion, String jooqVersion, boolean expected) {
        if (expected) {
            JooqxVersion.validateJooq(jooqxVersion, jooqVersion);
        } else {
            Assertions.assertThrows(IncompatibleVersion.class,
                                    () -> JooqxVersion.validateJooq(jooqxVersion, jooqVersion),
                                    "Expected IncompatibleVersion exception");
        }
    }

}
