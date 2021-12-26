package io.zero88.jooqx.integtest.spi.pg;

import java.time.Duration;

import org.jooq.types.YearToSecond;

import io.vertx.pgclient.data.Interval;
import io.zero88.jooqx.datatype.DataTypeMapper;
import io.zero88.jooqx.datatype.JooqxConverter;
import io.zero88.jooqx.spi.pg.datatype.IntervalConverter;

public class CustomInterval implements DataTypeMapper<Interval, YearToSecond, Duration> {

    @Override
    public JooqxConverter<Interval, YearToSecond> jooqxConverter() {
        return new IntervalConverter();
    }

    @Override
    public Duration from(YearToSecond databaseObject) {
        return databaseObject == null ? null : databaseObject.toDuration();
    }

    @Override
    public YearToSecond to(Duration userObject) {
        return userObject == null ? null : YearToSecond.valueOf(userObject);
    }

    @Override
    public Class<YearToSecond> fromType() {
        return YearToSecond.class;
    }

    @Override
    public Class<Duration> toType() {
        return Duration.class;
    }

}
