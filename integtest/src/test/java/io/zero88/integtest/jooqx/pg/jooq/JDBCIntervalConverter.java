package io.zero88.integtest.jooqx.pg.jooq;

import java.sql.SQLException;

import org.jetbrains.annotations.NotNull;
import org.jooq.types.DayToSecond;
import org.jooq.types.YearToMonth;
import org.jooq.types.YearToSecond;
import org.postgresql.util.PGInterval;

import io.zero88.jooqx.datatype.JooqxConverter;

class JDBCIntervalConverter implements JooqxConverter<String, YearToSecond> {

    @Override
    public YearToSecond from(String vertxObject) {
        try {
            final PGInterval pgInterval = new PGInterval(vertxObject);
            return new YearToSecond(new YearToMonth(pgInterval.getYears(), pgInterval.getMonths()),
                                    new DayToSecond(pgInterval.getDays(), pgInterval.getHours(),
                                                    pgInterval.getMinutes(), pgInterval.getWholeSeconds(),
                                                    pgInterval.getMicroSeconds() * 1000));
        } catch (SQLException e) {
            return YearToSecond.valueOf(vertxObject);
        }
    }

    @Override
    public String to(YearToSecond jooqObject) {
        return jooqObject.toString();
    }

    @Override
    public @NotNull Class<String> fromType() {
        return String.class;
    }

    @Override
    public @NotNull Class<YearToSecond> toType() {
        return YearToSecond.class;
    }

}