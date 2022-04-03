package io.zero88.jooqx.spi.pg.datatype;

import java.time.Duration;

import org.jetbrains.annotations.NotNull;
import org.jooq.types.YearToSecond;

import io.vertx.pgclient.data.Interval;
import io.zero88.jooqx.datatype.DataTypeMapper;
import io.zero88.jooqx.datatype.JooqxConverter;

public final class DurationConverter implements DataTypeMapper<Interval, YearToSecond, Duration> {

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
    public @NotNull Class<YearToSecond> fromType() {
        return YearToSecond.class;
    }

    @Override
    public @NotNull Class<Duration> toType() {
        return Duration.class;
    }

}
