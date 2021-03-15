package io.zero88.jooqx.spi.pg.datatype;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jooq.types.DayToSecond;
import org.jooq.types.YearToMonth;
import org.jooq.types.YearToSecond;

import io.vertx.pgclient.data.Interval;
import io.zero88.jooqx.datatype.JooqxConverter;

public final class IntervalConverter implements JooqxConverter<Interval, YearToSecond> {

    @Override
    public YearToSecond from(Interval vertxObject) {
        if (Objects.isNull(vertxObject)) {
            return null;
        }
        return new YearToSecond(new YearToMonth(vertxObject.getYears(), vertxObject.getMonths()),
                                new DayToSecond(vertxObject.getDays(), vertxObject.getHours(), vertxObject.getMinutes(),
                                                vertxObject.getSeconds(), vertxObject.getMicroseconds()));
    }

    @Override
    public Interval to(YearToSecond jooqObject) {
        if (Objects.isNull(jooqObject)) {
            return null;
        }
        return Interval.of(jooqObject.getYears(), jooqObject.getMonths(), jooqObject.getDays(), jooqObject.getHours(),
                           jooqObject.getMinutes(), jooqObject.getSeconds(), jooqObject.getMicro());
    }

    @Override
    public @NotNull Class<Interval> fromType() { return Interval.class; }

    @Override
    public @NotNull Class<YearToSecond> toType() { return YearToSecond.class; }

}
