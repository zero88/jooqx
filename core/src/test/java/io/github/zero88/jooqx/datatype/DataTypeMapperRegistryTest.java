package io.github.zero88.jooqx.datatype;

import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.datatype.basic.JsonArrayJSONConverter;
import io.github.zero88.jooqx.datatype.basic.JsonObjectJSONConverter;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

class DataTypeMapperRegistryTest {

    @Test
    void test_registry_usertype_as_jooq() {
        final DataTypeMapperRegistry registry = new DataTypeMapperRegistry();
        final Field<Object> field1 = DSL.field(DSL.name("table", "JsonArray"));
        final Field<Object> field2 = DSL.field(DSL.name("table", "any"));
        registry.add(UserTypeAsJooqType.create(new JsonObjectJSONConverter()))
                .addByColumn(field1, UserTypeAsJooqType.create(new JsonArrayJSONConverter()));
        final DataTypeMapper<Object, JSON, Object> mapper = registry.lookup(field1, JSON.class);
        Assertions.assertEquals(JsonArray.class, mapper.jooqxConverter().fromType());
        Assertions.assertEquals(JSON.class, mapper.jooqxConverter().toType());
        Assertions.assertEquals(JSON.class, mapper.fromType());
        Assertions.assertEquals(JSON.class, mapper.toType());
        final DataTypeMapper<Object, JSON, Object> any = registry.lookup(field2, JSON.class);
        Assertions.assertEquals(JsonObject.class, any.jooqxConverter().fromType());
        Assertions.assertEquals(JSON.class, any.jooqxConverter().toType());
        Assertions.assertEquals(JSON.class, any.fromType());
        Assertions.assertEquals(JSON.class, any.toType());
    }

    @Test
    void test_registry_usertype_as_vertx() {
        final DataTypeMapperRegistry registry = new DataTypeMapperRegistry();
        final Field<Object> field1 = DSL.field(DSL.name("table", "JsonArray"));
        final Field<Object> field2 = DSL.field(DSL.name("table", "any"));
        registry.add(UserTypeAsVertxType.create(new JsonObjectJSONConverter()))
                .addByColumn(field1, UserTypeAsVertxType.create(new JsonArrayJSONConverter()));
        final DataTypeMapper<Object, JSON, Object> mapper = registry.lookup(field1, JSON.class);
        Assertions.assertEquals(JsonArray.class, mapper.jooqxConverter().fromType());
        Assertions.assertEquals(JSON.class, mapper.jooqxConverter().toType());
        Assertions.assertEquals(JSON.class, mapper.fromType());
        Assertions.assertEquals(JsonArray.class, mapper.toType());
        final DataTypeMapper<Object, JSON, Object> any = registry.lookup(field2, JSON.class);
        Assertions.assertEquals(JsonObject.class, any.jooqxConverter().fromType());
        Assertions.assertEquals(JSON.class, any.jooqxConverter().toType());
        Assertions.assertEquals(JSON.class, any.fromType());
        Assertions.assertEquals(JsonObject.class, any.toType());
    }

}
