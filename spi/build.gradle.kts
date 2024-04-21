dependencies {
    api(project(":jooqx"))
//    api(ZeroLibs.rql_jooq)
    compileOnly(VertxLibs.jdbc)
    compileOnly(VertxLibs.pgsql)
    compileOnly(VertxLibs.mysql)
    compileOnly(VertxLibs.mssql)
    compileOnly(VertxLibs.db2)
    compileOnly(libs.jdbcH2)
    compileOnly(libs.jdbcDerby)
    compileOnly(libs.jdbcHSQL)
    compileOnly(libs.jdbcSQLite)

    testImplementation(testFixtures(project(":jooqx")))
    testImplementation(VertxLibs.jdbc)
    testImplementation(VertxLibs.pgsql)
    testImplementation(VertxLibs.mysql)
    testImplementation(VertxLibs.mssql)
    testImplementation(VertxLibs.db2)

}
