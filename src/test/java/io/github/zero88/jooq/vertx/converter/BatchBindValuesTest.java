package io.github.zero88.jooq.vertx.converter;

import java.util.List;
import java.util.Map;

import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class BatchBindValuesTest {

    @Test
    void test_register_dummy_value() {
        final BatchBindValues bindValues = new BatchBindValues().register("sql")
                                                                .register(DSL.field("is"),
                                                                          DSL.defaultValue(Boolean.class))
                                                                .register("awesome");
        final List<String> mappingFields = bindValues.getMappingFields();
        final List<Object> mappingValues = bindValues.getMappingValues();
        final Map<?, ?> dummyValues = bindValues.getDummyValues();
        Assertions.assertEquals(3, mappingFields.size());
        Assertions.assertEquals(3, mappingValues.size());
        Assertions.assertEquals(3, dummyValues.size());
        Assertions.assertEquals("sql is awesome", String.join(" ", mappingFields));
        Assertions.assertNull(mappingValues.get(0));
        Assertions.assertEquals(DSL.defaultValue(), mappingValues.get(1));
        Assertions.assertNull(mappingValues.get(2));
    }

}
