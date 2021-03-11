dependencies {
    api(project(":core"))
//    api(ZeroLibs.rql_jooq)
    compileOnly(VertxLibs.sqlClient)
    compileOnly(VertxLibs.jdbc)
    compileOnly(VertxLibs.pgsql)
    compileOnly(VertxLibs.mysql)
    compileOnly(VertxLibs.mssql)
    compileOnly(VertxLibs.db2)
}
