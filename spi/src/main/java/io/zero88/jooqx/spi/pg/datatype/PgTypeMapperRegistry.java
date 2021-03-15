package io.zero88.jooqx.spi.pg.datatype;

import io.zero88.jooqx.datatype.DataTypeMapperRegistry;
import io.zero88.jooqx.datatype.UserTypeAsJooqType;
import io.zero88.jooqx.datatype.UserTypeAsVertxType;
import io.zero88.jooqx.datatype.basic.BigDecimalConverter;
import io.zero88.jooqx.datatype.basic.BytesConverter;
import io.zero88.jooqx.datatype.basic.JsonObjectJSONBConverter;
import io.zero88.jooqx.datatype.basic.JsonObjectJSONConverter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PgTypeMapperRegistry {

    public static DataTypeMapperRegistry useUserTypeAsVertxType() {
        return new DataTypeMapperRegistry().add(UserTypeAsVertxType.create(new BytesConverter()))
                                           .add(UserTypeAsVertxType.create(new BigDecimalConverter()))
                                           .add(UserTypeAsVertxType.create(new JsonObjectJSONConverter()))
                                           .add(UserTypeAsVertxType.create(new JsonObjectJSONBConverter()))
                                           .add(UserTypeAsVertxType.create(new IntervalConverter()));
    }

    public static DataTypeMapperRegistry useUserTypeAsJooqType() {
        return new DataTypeMapperRegistry().add(UserTypeAsJooqType.create(new BytesConverter()))
                                           .add(UserTypeAsJooqType.create(new BigDecimalConverter()))
                                           .add(UserTypeAsJooqType.create(new JsonObjectJSONConverter()))
                                           .add(UserTypeAsJooqType.create(new JsonObjectJSONBConverter()))
                                           .add(UserTypeAsJooqType.create(new IntervalConverter()));
    }

}
