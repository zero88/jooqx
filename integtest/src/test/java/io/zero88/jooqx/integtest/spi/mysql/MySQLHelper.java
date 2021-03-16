package io.zero88.jooqx.integtest.spi.mysql;

import org.jooq.SQLDialect;

import io.zero88.jooqx.JooqSQL;
import io.zero88.jooqx.SQLTestHelper;
import io.zero88.jooqx.integtest.mysql.DefaultCatalog;
import io.zero88.jooqx.integtest.mysql.Testdb;

import lombok.NonNull;

public interface MySQLHelper extends JooqSQL<Testdb>, SQLTestHelper {

    @Override
    default @NonNull SQLDialect dialect() {
        return SQLDialect.MYSQL;
    }

    @Override
    default Testdb schema() {
        return DefaultCatalog.DEFAULT_CATALOG.TESTDB;
    }

}
