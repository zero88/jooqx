dependencies {
    api(projects.jooqx)
//    api(ZeroLibs.rql_jooq)
    compileOnly(libs.jdbcVertx)
    compileOnly(libs.postgresVertx)
    compileOnly(libs.mysqlVertx)
    compileOnly(libs.mssqlVertx)
    compileOnly(libs.db2Vertx)
    compileOnly(libs.h2Jdbc)
    compileOnly(libs.derbyJdbc)
    compileOnly(libs.hsqlJdbc)
    compileOnly(libs.sqliteJdbc)

    testImplementation(testFixtures(projects.jooqx))
    testImplementation(libs.jdbcVertx)
    testImplementation(libs.postgresVertx)
    testImplementation(libs.mysqlVertx)
    testImplementation(libs.mssqlVertx)
    testImplementation(libs.db2Vertx)

}
