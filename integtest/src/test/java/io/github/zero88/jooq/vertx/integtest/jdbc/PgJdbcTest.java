package io.github.zero88.jooq.vertx.integtest.jdbc;

import io.github.zero88.jooq.vertx.BaseVertxReactiveSql;
import io.github.zero88.jooq.vertx.spi.PostgreSQLTest.PostgreSQLReactiveTest;
import io.github.zero88.jooq.vertx.integtest.PostgreSQLHelper;
import io.github.zero88.jooq.vertx.integtest.pgsql.DefaultCatalog;

class PgJdbcTest extends BaseVertxReactiveSql<DefaultCatalog> implements PostgreSQLReactiveTest, PostgreSQLHelper {

}
