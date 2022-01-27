dependencies {
    testImplementation(project(":sample:model"))
    testImplementation(project(":sample:model2"))
    testImplementation(testFixtures(project(":jooqx-core")))
    testImplementation(project(":rsql:jooq"))

    testImplementation(DatabaseLibs.agroalApi)
    testImplementation(DatabaseLibs.agroalPool)
    testImplementation(DatabaseLibs.hikari)
    testImplementation(DatabaseLibs.jooqMeta)

    testImplementation(VertxLibs.jdbc)
    testImplementation(DatabaseLibs.h2)

    testImplementation(DatabaseLibs.pgsql)
    testImplementation(VertxLibs.pgsql)
    testImplementation(TestContainers.pgsql)

    testImplementation(DatabaseLibs.mysql)
    testImplementation(VertxLibs.mysql)
    testImplementation(TestContainers.mysql)

    testImplementation(VertxLibs.rx2)

    testImplementation(LogLibs.logback)
}
