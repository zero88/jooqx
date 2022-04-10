package io.github.zero88.jooqx.spi.pg.datatype;

import org.jooq.types.DayToSecond;
import org.jooq.types.YearToMonth;
import org.jooq.types.YearToSecond;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.vertx.pgclient.data.Interval;

class IntervalConverterTest {

    @Test
    void test_interval() {
        final IntervalConverter intervalConverter = new IntervalConverter();
        final Interval interval = Interval.of(1, 3, 5, 7, 9, 10, 2000);
        final YearToSecond from = intervalConverter.from(interval);
        final YearToSecond expected = new YearToSecond(new YearToMonth(1, 3), new DayToSecond(5, 7, 9, 10, 2000000));
        Assertions.assertEquals(expected, from);
        Assertions.assertEquals(interval, intervalConverter.to(from));
    }

}
