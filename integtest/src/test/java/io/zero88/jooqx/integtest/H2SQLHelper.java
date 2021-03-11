package io.zero88.jooqx.integtest;

import org.jooq.SQLDialect;

import io.zero88.jooqx.JooqSQL;
import io.zero88.jooqx.integtest.h2.DefaultCatalog;

import lombok.NonNull;

public interface H2SQLHelper extends JooqSQL<DefaultCatalog>, SQLTestHelper {

    @Override
    default @NonNull SQLDialect dialect() {
        return SQLDialect.H2;
    }

    @Override
    default DefaultCatalog catalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

}
