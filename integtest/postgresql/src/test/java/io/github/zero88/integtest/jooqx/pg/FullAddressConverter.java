package io.github.zero88.integtest.jooqx.pg;

import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.jooqx.datatype.JooqxConverter;
import io.github.zero88.jooqx.datatype.basic.UDTParser;
import io.github.zero88.sample.model.pgsql.udt.FullAddress;
import io.github.zero88.sample.model.pgsql.udt.records.FullAddressRecord;

public class FullAddressConverter implements JooqxConverter<String, FullAddressRecord> {

    @Override
    public FullAddressRecord from(String vertxObject) {
        String[] udt = UDTParser.parse(vertxObject);
        if (udt == null) {
            return null;
        }
        System.out.println(vertxObject + "::" + Arrays.toString(udt));
        return new FullAddressRecord().with(FullAddress.STATE, udt[0])
                                      .with(FullAddress.CITY, udt[1])
                                      .with(FullAddress.STREET, udt[2])
                                      .with(FullAddress.NOA, Integer.valueOf(udt[3]))
                                      .with(FullAddress.HOME, "t".equals(udt[4]));
    }

    @Override
    public String to(FullAddressRecord jooqObject) {
        return jooqObject.toString();
    }

    @Override
    public @NotNull Class<String> fromType() {
        return String.class;
    }

    @Override
    public @NotNull Class<FullAddressRecord> toType() {
        return FullAddressRecord.class;
    }

}
