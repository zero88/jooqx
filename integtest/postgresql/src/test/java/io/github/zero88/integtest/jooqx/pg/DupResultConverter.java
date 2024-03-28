package io.github.zero88.integtest.jooqx.pg;

import org.jetbrains.annotations.NotNull;

import io.github.zero88.jooqx.datatype.JooqxConverter;
import io.github.zero88.jooqx.datatype.basic.UDTParser;
import io.github.zero88.sample.model.pgsql.udt.DupResult;
import io.github.zero88.sample.model.pgsql.udt.records.DupResultRecord;

public class DupResultConverter implements JooqxConverter<String, DupResultRecord> {

    @Override
    public DupResultRecord from(String vertxObject) {
        String[] udt = UDTParser.parse(vertxObject);
        if (udt == null) {
            return null;
        }
        return new DupResultRecord().with(DupResult.F1, DupResult.F1.getDataType().convert(udt[0]))
                                    .with(DupResult.F2, udt[1]);
    }

    @Override
    public String to(DupResultRecord jooqObject) {
        return jooqObject.toString();
    }

    @Override
    public @NotNull Class<String> fromType() {
        return String.class;
    }

    @Override
    public @NotNull Class<DupResultRecord> toType() {
        return DupResultRecord.class;
    }

}
