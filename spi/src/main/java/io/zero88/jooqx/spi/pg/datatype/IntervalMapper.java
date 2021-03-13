package io.zero88.jooqx.spi.pg.datatype;

import java.util.Objects;

import org.jooq.types.DayToSecond;
import org.jooq.types.YearToMonth;
import org.jooq.types.YearToSecond;

import io.vertx.pgclient.data.Interval;
import io.zero88.jooqx.datatype.IdentityMapper;

public final class IntervalMapper implements IdentityMapper<Interval, YearToSecond> {

    @Override
    public Interval from(YearToSecond databaseObject) {
        if (Objects.isNull(databaseObject)) {
            return null;
        }
        return Interval.of(databaseObject.getYears(), databaseObject.getMonths(), databaseObject.getDays(),
                           databaseObject.getHours(), databaseObject.getMinutes(), databaseObject.getSeconds(),
                           databaseObject.getMicro());
    }

    @Override
    public YearToSecond to(Interval userObject) {
        if (Objects.isNull(userObject)) {
            return null;
        }
        return new YearToSecond(new YearToMonth(userObject.getYears(), userObject.getMonths()),
                                new DayToSecond(userObject.getDays(), userObject.getHours(), userObject.getMinutes(),
                                                userObject.getSeconds(), userObject.getMicroseconds()));
    }

    @Override
    public Class<YearToSecond> fromType() {
        return YearToSecond.class;
    }

    @Override
    public Class<Interval> toType() {
        return Interval.class;
    }

}
