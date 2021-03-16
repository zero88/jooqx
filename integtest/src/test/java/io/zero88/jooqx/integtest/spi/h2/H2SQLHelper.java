package io.zero88.jooqx.integtest.spi.h2;

import org.jooq.SQLDialect;

import io.zero88.jooqx.JooqSQL;
import io.zero88.jooqx.SQLTestHelper;
import io.zero88.jooqx.integtest.h2.DefaultCatalog;
import io.zero88.jooqx.integtest.h2.DefaultSchema;

import lombok.NonNull;

public interface H2SQLHelper extends JooqSQL<DefaultSchema>, SQLTestHelper {

    @Override
    default @NonNull SQLDialect dialect() {
        return SQLDialect.H2;
    }

    @Override
    default DefaultSchema schema() {
        return DefaultCatalog.DEFAULT_CATALOG.DEFAULT_SCHEMA;
    }

}
