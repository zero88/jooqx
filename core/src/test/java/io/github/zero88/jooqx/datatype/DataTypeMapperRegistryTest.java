package io.github.zero88.jooqx.datatype;

import org.jooq.Converter;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.zero88.jooqx.datatype.basic.JsonArrayJSONConverter;
import io.github.zero88.jooqx.datatype.basic.JsonObjectJSONConverter;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@SuppressWarnings("rawtypes")
class DataTypeMapperRegistryTest {

    @Test
    void test_registry_usertype_as_jooq() {
        final DataTypeMapperRegistry registry = new DataTypeMapperRegistry();
        final Field<Object> field1 = DSL.field(DSL.name("table", "JsonArray"));
        final Field<Object> field2 = DSL.field(DSL.name("table", "any"));
        registry.add(UserTypeAsJooqType.create(new JsonObjectJSONConverter()))
                .addByColumn(field1, UserTypeAsJooqType.create(new JsonArrayJSONConverter()));

        final Converter field1Mapper = registry.lookup(field1, JSON.class);
        Assertions.assertInstanceOf(DataTypeMapper.class, field1Mapper);
        final JooqxConverter field1JooqxConverter = ((DataTypeMapper) field1Mapper).jooqxConverter();
        Assertions.assertEquals(JsonArray.class, field1JooqxConverter.fromType());
        Assertions.assertEquals(JSON.class, field1JooqxConverter.toType());
        Assertions.assertEquals(JSON.class, field1Mapper.fromType());
        Assertions.assertEquals(JSON.class, field1Mapper.toType());

        final Converter field2Mapper = registry.lookup(field2, JSON.class);
        Assertions.assertInstanceOf(DataTypeMapper.class, field2Mapper);
        final JooqxConverter field2JooqxConverter = ((DataTypeMapper) field2Mapper).jooqxConverter();
        Assertions.assertEquals(JsonObject.class, field2JooqxConverter.fromType());
        Assertions.assertEquals(JSON.class, field2JooqxConverter.toType());
        Assertions.assertEquals(JSON.class, field2Mapper.fromType());
        Assertions.assertEquals(JSON.class, field2Mapper.toType());
    }

    @Test
    void test_registry_usertype_as_vertx() {
        final DataTypeMapperRegistry registry = new DataTypeMapperRegistry();
        final Field<Object> field1 = DSL.field(DSL.name("table", "JsonArray"));
        final Field<Object> field2 = DSL.field(DSL.name("table", "any"));
        registry.add(UserTypeAsVertxType.create(new JsonObjectJSONConverter()))
                .addByColumn(field1, UserTypeAsVertxType.create(new JsonArrayJSONConverter()));

        final Converter field1Mapper = registry.lookup(field1, JSON.class);
        Assertions.assertInstanceOf(DataTypeMapper.class, field1Mapper);
        final JooqxConverter field1JooqxConverter = ((DataTypeMapper) field1Mapper).jooqxConverter();
        Assertions.assertEquals(JsonArray.class, field1JooqxConverter.fromType());
        Assertions.assertEquals(JSON.class, field1JooqxConverter.toType());
        Assertions.assertEquals(JSON.class, field1Mapper.fromType());
        Assertions.assertEquals(JsonArray.class, field1Mapper.toType());

        final Converter field2Mapper = registry.lookup(field2, JSON.class);
        Assertions.assertInstanceOf(DataTypeMapper.class, field1Mapper);
        final JooqxConverter field2JooqxConverter = ((DataTypeMapper) field2Mapper).jooqxConverter();
        Assertions.assertEquals(JsonObject.class, field2JooqxConverter.fromType());
        Assertions.assertEquals(JSON.class, field2JooqxConverter.toType());
        Assertions.assertEquals(JSON.class, field2Mapper.fromType());
        Assertions.assertEquals(JsonObject.class, field2Mapper.toType());
    }

}
