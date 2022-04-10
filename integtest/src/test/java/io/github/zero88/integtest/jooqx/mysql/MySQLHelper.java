package io.github.zero88.integtest.jooqx.mysql;

import org.jetbrains.annotations.NotNull;
import org.jooq.SQLDialect;

import io.github.zero88.jooqx.JooqSQL;
import io.github.zero88.jooqx.SQLTestHelper;
import io.zero88.sample.data.mysql.DefaultCatalog;
import io.zero88.sample.data.mysql.Testdb;

public interface MySQLHelper extends JooqSQL<Testdb>, SQLTestHelper {

    @Override
    default @NotNull SQLDialect dialect() {
        return SQLDialect.MYSQL;
    }

    @Override
    default Testdb schema() {
        return DefaultCatalog.DEFAULT_CATALOG.TESTDB;
    }

}
