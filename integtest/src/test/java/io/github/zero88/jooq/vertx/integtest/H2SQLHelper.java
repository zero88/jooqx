package io.github.zero88.jooq.vertx.integtest;

import org.jooq.SQLDialect;

import io.github.zero88.jooq.vertx.JooqSQL;
import io.github.zero88.jooq.vertx.integtest.h2.DefaultCatalog;

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
