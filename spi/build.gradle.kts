dependencies {
    api(project(":core"))
//    api(ZeroLibs.rql_jooq)
    compileOnly(VertxLibs.sqlClient)
    compileOnly(VertxLibs.jdbc)
    compileOnly(VertxLibs.pgsql)
    compileOnly(VertxLibs.mysql)
    compileOnly(VertxLibs.mssql)
    compileOnly(VertxLibs.db2)

    testImplementation(testFixtures(project(":core")))
    testImplementation(VertxLibs.sqlClient)
    testImplementation(VertxLibs.jdbc)
    testImplementation(VertxLibs.pgsql)
    testImplementation(VertxLibs.mysql)
    testImplementation(VertxLibs.mssql)
    testImplementation(VertxLibs.db2)

}
