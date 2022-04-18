package io.github.zero88.integtest.jooqx.mysql;

import org.jetbrains.annotations.NotNull;
import org.jooq.SQLDialect;

import io.github.zero88.jooqx.JooqSQL;
import io.github.zero88.jooqx.SQLTestHelper;
import io.github.zero88.sample.model.mysql.DefaultCatalog;
import io.github.zero88.sample.model.mysql.Test;

public interface MySQLHelper extends JooqSQL<Test>, SQLTestHelper {

    @Override
    default @NotNull SQLDialect dialect() {
        return SQLDialect.MYSQL;
    }

    @Override
    default Test schema() {
        return DefaultCatalog.DEFAULT_CATALOG.TEST;
    }

}
