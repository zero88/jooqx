package io.github.zero88.integtest.jooqx.pg;

import org.jooq.impl.EnumConverter;

import io.github.zero88.jooqx.datatype.JooqxConverter;
import io.github.zero88.sample.model.pgsql.enums.Mood;

public class EnumMoodConverter extends EnumConverter<String, Mood> implements JooqxConverter<String, Mood> {

    public EnumMoodConverter() {
        super(String.class, Mood.class);
    }

}
