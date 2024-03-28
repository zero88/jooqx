package io.github.zero88.integtest.jooqx.pg;

import org.jooq.impl.EnumConverter;

import io.github.zero88.jooqx.datatype.JooqxConverter;
import io.github.zero88.sample.model.pgsql.enums.Weather;

public class EnumWeatherConverter extends EnumConverter<String, Weather> implements JooqxConverter<String, Weather> {

    public EnumWeatherConverter() {
        super(String.class, Weather.class);
    }

}
