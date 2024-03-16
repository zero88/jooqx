dependencies {
    api(project(":jooqx"))
//    api(ZeroLibs.rql_jooq)
    compileOnly(VertxLibs.jdbc)
    compileOnly(VertxLibs.pgsql)
    compileOnly(VertxLibs.mysql)
    compileOnly(VertxLibs.mssql)
    compileOnly(VertxLibs.db2)
    compileOnly(DatabaseLibs.h2)
    compileOnly(DatabaseLibs.derby)
    compileOnly(DatabaseLibs.hsqldb)
    compileOnly(DatabaseLibs.sqlite)

    testImplementation(testFixtures(project(":jooqx")))
    testImplementation(VertxLibs.jdbc)
    testImplementation(VertxLibs.pgsql)
    testImplementation(VertxLibs.mysql)
    testImplementation(VertxLibs.mssql)
    testImplementation(VertxLibs.db2)

    tasks {
        jar {
            archiveClassifier.set("jdk17")
        }
    }
}
